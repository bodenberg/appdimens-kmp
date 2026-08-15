/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens.git
 * Date: 2025-10-04 | Optimized: 2026-03-31 | KMP port: 2026-08
 *
 * Library: AppDimens — Global Dimension Cache Manager
 *
 * Description:
 * Ultra-optimized, lock-free, shared cache for all AppDimens dimension calculations.
 * Works for both `compose` and `code` (non-Compose) packages.
 *
 * Key Design Principles:
 *  - Snapshot-partitioned cache: each window/configuration snapshot (DimenMetrics)
 *    owns a bounded partition; entries are published as one immutable atomic reference
 *  - Collision-safe via packed 64-bit Long key (no false hits)
 *  - Shared state across all library instances (save memory, share reuse)
 *  - Per-snapshot correctness: rotated / resized / recreated windows never read stale values
 *  - Zero allocation in hot path: stores raw Float, caller boxes into Dp/TextUnit
 *
 * KMP port notes:
 *  - `java.util.concurrent.atomic.*` → `kotlin.concurrent.atomics.*` (JVM/Android map to
 *    the same primitives; native gets a correct implementation)
 *  - `java.util.concurrent.LongAdder` counters → `AtomicLong` (diagnostics are off by default)
 *  - `CopyOnWriteArrayList` → synchronized list
 *  - `WeakHashMap` → [weakIdentityMap] expect/actual (JVM: weak identity map; native: bounded)
 *  - `ThreadLocal` metrics scope → [MetricsScopeHolder] expect/actual (JVM: ThreadLocal;
 *    native: @Volatile)
 *  - `android.content.Context` → [AppDimensContext] (platform-neutral window handle)
 *  - `android.content.res.Configuration` → [ScreenConfiguration] snapshot
 *
 * Bit Layout of the 64-bit Cache Key (Long):
 *  [63]     applyAspectRatio          1 bit
 *  [62-31]  baseValue bits            32 bits  (Float.toRawBits)
 *  [30-27]  CalcType ordinal          4 bits  (covers 0..15)
 *  [26-24]  ValueType                 3 bits  (covers 0..7)
 *  [23-8]   sensitivityK fingerprint  16 bits (float bits ushr 16 & 0xFFFF)
 *  [7-6]    DpQualifier ordinal       2 bits  (covers 0..3)
 *  [5-2]    Inverter ordinal          4 bits  (covers 0..15)
 *  [1]      isLandscape               1 bit
 *  [0]      ignoreMultiWindows        1 bit
 *
 * Licensed under the Apache License, Version 2.0
 */
package com.appdimens.dynamic.core

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import com.appdimens.dynamic.common.UiModeType
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.AtomicArray
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * EN
 * Global, lock-free, shared cache for all AppDimens dimension calculations.
 *
 * **Thread Safety**: Completely thread-safe.  Since 3.1.8 the cache is partitioned per
 * immutable window snapshot ([DimenMetrics]); each entry is published as a single
 * atomic [CacheEntry] (key + value bits) reference, so concurrent readers can never
 * observe another key's value.
 *
 * PT
 * Cache global, lock-free e compartilhado para todos os cálculos de dimensão do AppDimens.
 */
@OptIn(ExperimentalAtomicApi::class)
object DimenCache {
    private val resetListeners = mutableListOf<() -> Unit>()
    private val resetListenersLock = SynchronizedObject()

    /**
     * EN Registers a listener to be notified when the cache is cleared.
     * PT Registra um listener para ser notificado quando o cache for limpo.
     */
    fun addResetListener(listener: () -> Unit) {
        locked(resetListenersLock) { resetListeners.add(listener) }
    }

    /**
     * EN Removes a previously registered reset listener.
     * PT Remove um listener de reset previamente registrado.
     */
    fun removeResetListener(listener: () -> Unit) {
        locked(resetListenersLock) { resetListeners.remove(listener) }
    }

    private fun notifyResetListeners() {
        val snapshot = locked(resetListenersLock) { resetListeners.toList() }
        snapshot.forEach { it() }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONFIGURATION & INITIALIZATION STATE
    // ─────────────────────────────────────────────────────────────────────────

    internal val isInitializing = AtomicBoolean(false)

    /**
     * Internal flag to avoid [AtomicBoolean.get] overhead on every hot-path call.
     *
     * **Thread Safety**: marked `@Volatile` so that the `true` written during [init]
     * is immediately visible to all other threads without requiring a full memory
     * barrier on every read.  Without `@Volatile` a thread that reads this field on a
     * different CPU core may observe stale `false` indefinitely (data race /
     * visibility bug on ARM64 weak memory model).
     */
    @PublishedApi
    internal val isInitializedFast = AtomicBoolean(false)
    val isInitialized = AtomicBoolean(false)

    /**
     * EN Calculation types based on the library's package structure.
     * PT Tipos de cálculo baseados na estrutura de pacotes da biblioteca.
     */
    enum class CalcType {
        AUTO, DIAGONAL, FILL, FIT, FLUID, INTERPOLATED, LOGARITHMIC,
        PERCENT, PERIMETER, POWER, RESIZE, SCALED, UNITIES, ASPECT_RATIO, DENSITY
    }

    val CT_PERCENT       = CalcType.PERCENT.ordinal
    val CT_SCALED        = CalcType.SCALED.ordinal
    val CT_DENSITY       = CalcType.DENSITY.ordinal
    val CT_ASPECT_RATIO  = CalcType.ASPECT_RATIO.ordinal
    val CT_DIAGONAL      = CalcType.DIAGONAL.ordinal
    val CT_INTERPOLATED  = CalcType.INTERPOLATED.ordinal
    val CT_PERIMETER     = CalcType.PERIMETER.ordinal
    val CT_POWER         = CalcType.POWER.ordinal
    val CT_LOGARITHMIC   = CalcType.LOGARITHMIC.ordinal

    // ─────────────────────────────────────────────────────────────────────────
    // DIAGNOSTICS COUNTERS — guarded by [diagnosticsEnabled] to avoid overhead
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN When `true`, hit/miss/eviction counters are incremented on every cache
     * operation. Disabled by default so production apps pay zero overhead.
     *
     * PT Quando `true`, contadores de hit/miss/eviction são incrementados a cada
     * operação. Desativado por padrão para não penalizar apps em produção.
     */
    @PublishedApi
    internal val diagnosticsEnabled = AtomicBoolean(false)

    val hitCount      = AtomicLong(0L)
    val missCount     = AtomicLong(0L)
    val evictionCount = AtomicLong(0L)

    /**
     * EN Master switch for the cache system. If disabled, all calls will recompute.
     * PT Chave mestre para o sistema de cache. Se desativado, todos os cálculos são refeitos.
     */
    @PublishedApi
    internal val isEnabled = AtomicBoolean(true)

    // ─────────────────────────────────────────────────────────────────────────
    // CACHED UiModeType — avoids per-call UI-mode detection
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Snapshot of the [ScreenConfiguration] fields that drive cache invalidation.
     * PT Snapshot dos campos de [ScreenConfiguration] usados na invalidação.
     */
    internal data class ConfigSnapshot(
        val screenWidthDp: Int,
        val screenHeightDp: Int,
        val smallestScreenWidthDp: Int,
        val densityDpi: Int,
        val fontScale: Float,
    ) {
        companion object {
            fun from(c: ScreenConfiguration) = ConfigSnapshot(
                screenWidthDp = c.screenWidthDp,
                screenHeightDp = c.screenHeightDp,
                smallestScreenWidthDp = c.smallestScreenWidthDp,
                densityDpi = c.densityDpi,
                fontScale = c.fontScale,
            )
        }
    }

    private var lastConfiguration: ConfigSnapshot? = null

    internal val cachedUiMode = AtomicReference<UiModeType>(UiModeType.UNDEFINED)

    private data class UiModeCacheEntry(val fingerprint: Int, val value: UiModeType)

    /**
     * Per-context cache: the value never retains its weak key, so a window/context can be
     * collected normally. A process-wide single entry is incorrect when two windows differ.
     */
    private val uiModeByContextLock = SynchronizedObject()

    private val uiModeByContext = weakIdentityMap<AppDimensContext, UiModeCacheEntry>()

    /**
     * EN Resolves the [UiModeType] for [context], fingerprint-cached per window.
     * PT Resolve o [UiModeType] de [context], com cache por janela (fingerprint).
     */
    fun getCachedUiModeType(context: AppDimensContext?): UiModeType {
        if (context == null) return cachedUiMode.load()
        val cfg = context.configuration
        // Fingerprint only fields that affect UiMode / foldable detection.
        val fingerprint =
            (cfg.uiMode * 31 + cfg.smallestScreenWidthDp) * 31 +
                min(cfg.screenWidthDp, cfg.screenHeightDp) * 31 +
                max(cfg.screenWidthDp, cfg.screenHeightDp)
        return locked(uiModeByContextLock) {
            val cached = uiModeByContext[context]
            if (cached?.fingerprint == fingerprint) {
                cached.value
            } else {
                val mode = context.uiModeType
                uiModeByContext[context] = UiModeCacheEntry(fingerprint, mode)
                // Deprecated global field is updated for callers that inspect it, but is
                // never read as a source of truth.
                cachedUiMode.store(mode)
                mode
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SCREEN FACTORS — padded object to prevent false sharing on @Volatile fields
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Holds all screen-derived scaling factors in an object padded to exceed two ARM64
     * cache lines (2 × 64 bytes = 128 bytes), ensuring that writes during
     * [updateFactors] do not invalidate unrelated reads on sibling CPU cores.
     *
     * PT Agrupa todos os fatores de escala derivados da tela em um objeto com padding de
     * 128 bytes, prevenindo false sharing entre núcleos durante [updateFactors].
     */
    internal class ScreenFactors {
        var normalizedAr: Float = 1.0f
        var logNormalizedAr: Float = 0f
        var smallestWidthDp: Int   = 0
        var density: Float = 1.0f
        var scale: Float = 1.0f
        var arMultiplier: Float = 1.0f
        // Shared AR multiply helper (used by many strategies' custom-AR paths).
        var aspectRatioMul: Float = 1.0f
        // 128-byte padding guard (8 × Long = 64 bytes + object fields overhead ≥ 128)
        @Suppress("unused") val _p0 = 0L
        @Suppress("unused") val _p1 = 0L
        @Suppress("unused") val _p2 = 0L
        @Suppress("unused") val _p3 = 0L
        @Suppress("unused") val _p4 = 0L
        @Suppress("unused") val _p5 = 0L
        @Suppress("unused") val _p6 = 0L
        @Suppress("unused") val _p7 = 0L
    }

    @PublishedApi
    internal val factors = ScreenFactors()

    /**
     * Compatibility view used by existing strategy modules while they are migrated to
     * explicit [DimenMetrics] parameters.  During a resolution it is the exact immutable
     * metrics supplied to that call; it is never a partially updated global factor set.
     */
    @PublishedApi
    internal val metricsScope
        get() = MetricsScopeHolder.current

    @PublishedApi
    internal val fallbackMetrics = AtomicReference<DimenMetrics>(DimenMetrics.DEFAULT)

    val currentMetrics: DimenMetrics
        get() = metricsScope ?: fallbackMetrics.load()

    // Convenience accessors — public so satellite modules can read a coherent snapshot.
    val currentNormalizedAr      get() = currentMetrics.normalizedAspectRatio
    val currentLogNormalizedAr   get() = currentMetrics.logNormalizedAspectRatio
    val currentSmallestWidthDp   get() = currentMetrics.smallestWidthDp.toInt()
    val currentDensity           get() = currentMetrics.density
    val currentScale             get() = currentMetrics.scale
    val currentArMultiplier      get() = currentMetrics.defaultScaledAspectRatioMultiplier
    val currentAspectRatioMul    get() = currentMetrics.defaultAspectRatioMultiplier

    // ─────────────────────────────────────────────────────────────────────────
    // SNAPSHOT-PARTITIONED CACHE
    // ─────────────────────────────────────────────────────────────────────────

    /** Four active window/configuration snapshots × 512 entries = the former 2048-slot budget. */
    private const val MAX_SNAPSHOT_CACHES = 4
    private const val SNAPSHOT_CACHE_SIZE = 2048 / MAX_SNAPSHOT_CACHES
    private const val SNAPSHOT_CACHE_MASK = SNAPSHOT_CACHE_SIZE - 1

    /**
     * A key and its raw Float bits are published as one immutable reference.  The previous
     * two-array design could expose a key written by one thread with a value written by
     * another.  A single atomic reference is a correctness boundary, not a micro-optimization.
     */
    @PublishedApi
    internal data class CacheEntry(val key: Long, val valueBits: Int)

    /**
     * KMP note: `kotlin.concurrent.atomics.AtomicReferenceArray` requires non-null
     * elements, so an empty slot carries the [EMPTY_ENTRY] sentinel (key 0 with the
     * `Int.MIN_VALUE` value-bits marker) instead of `null`. A real entry can never
     * produce those exact bits in practice; at worst a pathological `-0.0f` result
     * would recompute on every call — never a wrong answer.
     */
    @PublishedApi
    internal class SnapshotCache(size: Int) {
        val entries = AtomicArray<CacheEntry>(size) { EMPTY_ENTRY }
    }

    @PublishedApi
    internal val EMPTY_ENTRY: CacheEntry = CacheEntry(0L, Int.MIN_VALUE)

    private val snapshotCaches = mutableMapOf<DimenMetrics, SnapshotCache>()
    private val snapshotCacheLock = SynchronizedObject()

    /**
     * EN Single @Volatile holder pairing the window context with its metrics so the
     *    hot lane pays ONE volatile load instead of two. The fields are final, so the
     *    single volatile write publishes both safely (no torn context/metrics pair).
     * PT Portador único @Volatile unindo contexto e métricas da janela para o caminho
     *    quente pagar UMA leitura volátil em vez de duas. Os campos são finais, então
     *    a única escrita volátil publica ambos com segurança (sem par rasgado).
     */
    @PublishedApi
    internal class FastWindowSlot(val context: AppDimensContext?, val metrics: DimenMetrics)

    @PublishedApi
    internal val EMPTY_FAST_WINDOW_SLOT = FastWindowSlot(null, DimenMetrics.DEFAULT)

    @PublishedApi
    internal val fastWindowSlot = AtomicReference<FastWindowSlot>(EMPTY_FAST_WINDOW_SLOT)

    private val metricsByWindowContextLock = SynchronizedObject()

    private val metricsByWindowContext = weakIdentityMap<AppDimensContext, DimenMetrics>()

    private class MwSlot(val context: AppDimensContext?, val mode: Boolean)

    private val EMPTY_MW_SLOT = MwSlot(null, false)

    private val fastMwSlot = AtomicReference<MwSlot>(EMPTY_MW_SLOT)

    // ─────────────────────────────────────────────────────────────────────────
    // EVENT-DRIVEN CONFIG WATCHER — a config listener registered on the platform
    // window invalidates the fast slots synchronously on any real configuration
    // change, so a non-null slot may be trusted by identity alone with zero
    // sampling cost on the hot lane.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Weak so a test/isolated context does not leak; the real window lives for the process.
     */
    private val watchedContexts = weakIdentityMap<AppDimensContext, Boolean>()
    private val watchedContextsLock = SynchronizedObject()

    /**
     * EN Registers the platform window listener exactly once per [AppDimensContext]
     * instance. Called only from [metricsFor] / [init] — never from the hot lane.
     * PT Registra o listener de janela uma única vez por instância de [AppDimensContext].
     */
    private fun ensureConfigWatcher(context: AppDimensContext) {
        locked(watchedContextsLock) {
            if (watchedContexts[context] != true) {
                watchedContexts[context] = true
                context.registerConfigurationListener { onContextConfigChanged(context) }
            }
        }
    }

    private fun onContextConfigChanged(context: AppDimensContext) {
        fastWindowSlot.store(EMPTY_FAST_WINDOW_SLOT)
        fastMwSlot.store(EMPTY_MW_SLOT)
        invalidateOnConfigChange(context.configuration)
    }

    private fun mwModeFor(context: AppDimensContext?): Boolean {
        val slot = fastMwSlot.load()
        if (slot.context === context) return slot.mode
        val rebuilt = context?.isInMultiWindowMode == true
        fastMwSlot.store(MwSlot(context, rebuilt))
        return rebuilt
    }

    private fun fastMatch(
        metrics: DimenMetrics,
        configuration: ScreenConfiguration,
        isMultiWindow: Boolean,
    ): Boolean =
        metrics.screenWidthDp == configuration.screenWidthDp &&
            metrics.screenHeightDp == configuration.screenHeightDp &&
            metrics.smallestScreenWidthDp == configuration.smallestScreenWidthDp &&
            metrics.densityDpi == configuration.densityDpi &&
            metrics.fontScaleBits == configuration.fontScale.toRawBits() &&
            metrics.orientation == configuration.orientation &&
            metrics.uiMode == configuration.uiMode &&
            metrics.isInMultiWindowMode == isMultiWindow

    @PublishedApi
    internal fun metricsFor(context: AppDimensContext?): DimenMetrics {
        if (context == null) return fallbackMetrics.load()
        ensureConfigWatcher(context)
        val fast = fastWindowSlot.load()
        if (fast !== EMPTY_FAST_WINDOW_SLOT && fast.context === context) {
            val cfg = context.configuration
            if (fastMatch(fast.metrics, cfg, mwModeFor(context))) {
                return fast.metrics
            }
        }
        // Slow path: (re)build and memo. The weak map handles an app with several
        // alternating windows; the @Volatile slot always mirrors the latest explicit call.
        val cached = locked(metricsByWindowContextLock) { metricsByWindowContext[context] }
        if (cached != null) {
            val cfg = context.configuration
            if (fastMatch(cached, cfg, mwModeFor(context))) {
                fastWindowSlot.store(FastWindowSlot(context, cached))
                return cached
            }
        }
        val rebuilt = DimenMetrics.from(
            screen = context.configuration,
            isInMultiWindowMode = context.isInMultiWindowMode,
        )
        locked(metricsByWindowContextLock) { metricsByWindowContext[context] = rebuilt }
        fastWindowSlot.store(FastWindowSlot(context, rebuilt))
        return rebuilt
    }

    // Single-window fast memo for the partition lookup. The typical app resolves
    // against one immutable snapshot for thousands of calls; the map hash+equals of
    // DimenMetrics would otherwise run on every cache hit. Multi-window apps simply
    // re-sync this pair each time the active window alternates (correct, and rare).
    @PublishedApi
    internal val fastPartitionMetrics = AtomicReference<DimenMetrics>(DimenMetrics.DEFAULT)

    @PublishedApi
    internal val EMPTY_PARTITION = SnapshotCache(0)

    @PublishedApi
    internal val fastPartition = AtomicReference<SnapshotCache>(EMPTY_PARTITION)

    @PublishedApi
    internal fun cacheFor(metrics: DimenMetrics): SnapshotCache = locked(snapshotCacheLock) {
        snapshotCaches[metrics] ?: run {
            // A resize can produce many transient configurations. Keep the total memory
            // budget fixed instead of turning the cache into a history of every pixel size.
            if (snapshotCaches.size >= MAX_SNAPSHOT_CACHES) {
                snapshotCaches.keys.firstOrNull { it !== metrics }?.let(snapshotCaches::remove)
            }
            SnapshotCache(SNAPSHOT_CACHE_SIZE).also { snapshotCaches[metrics] = it }
        }
    }

    @PublishedApi
    internal fun slotFor(key: Long): Int {
        val h = (key xor (key ushr 32)).toInt()
        val mixed = h xor (h ushr 16)
        return mixed and SNAPSHOT_CACHE_MASK
    }

    @PublishedApi
    internal inline fun <T> withMetrics(metrics: DimenMetrics, crossinline block: () -> T): T {
        val previous = MetricsScopeHolder.current
        if (previous === metrics) {
            return block()
        }
        MetricsScopeHolder.current = metrics
        return try {
            block()
        } finally {
            MetricsScopeHolder.current = previous
        }
    }

    /** Used by Compose helpers to make nested legacy strategy calls observe LocalDimenMetrics. */
    internal fun <T> withCompositionMetrics(metrics: DimenMetrics?, block: () -> T): T =
        if (metrics == null) block() else withMetrics(metrics, block)

    /**
     * Core resolution — inlined at every call site so the `compute` lambda is inlined
     * with zero object allocation.
     */
    @PublishedApi
    internal inline fun resolve(
        key: Long,
        metrics: DimenMetrics,
        crossinline compute: () -> Float,
    ): Float {
        // Custom-K keys only encode 16 bits of the 32-bit float (buildKey). Two different
        // K values could alias one slot and answer with the other's result, so they are
        // computed exactly on every call — never stored, never peek-able.
        if (!isEnabled.load() || hasCustomSensitivityKey(key) || shouldBypassCache(key)) {
            return withMetrics(metrics) { compute() }
        }

        var partition = fastPartition.load()
        if (partition === EMPTY_PARTITION || fastPartitionMetrics.load() !== metrics) {
            partition = cacheFor(metrics)
            fastPartition.store(partition)
            fastPartitionMetrics.store(metrics)
        }
        val slot = slotFor(key)
        val existing = partition.entries.loadAt(slot)
        if (existing.valueBits != EMPTY_ENTRY.valueBits && existing.key == key) {
            if (diagnosticsEnabled.load()) hitCount.fetchAndIncrement()
            return Float.fromBits(existing.valueBits)
        }

        if (diagnosticsEnabled.load()) missCount.fetchAndIncrement()
        val computed = withMetrics(metrics) { compute() }
        // Non-finite values are never useful cache entries and should not contaminate a
        // later valid request with the same key.
        if (!computed.isFinite()) return computed

        if (diagnosticsEnabled.load() && existing.valueBits != EMPTY_ENTRY.valueBits) evictionCount.fetchAndIncrement()
        partition.entries.storeAt(slot, CacheEntry(key, computed.toRawBits()))
        return computed
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MATH CONSTANTS
    // ─────────────────────────────────────────────────────────────────────────

    const val INV_BASE_RATIO      = 0.0033333334f // 1f / 300f
    const val ADJUSTMENT_SCALE    = 0.10f / 30f   // 0.0033333334f
    const val SENSITIVITY_DEFAULT = 0.08f / 30f   // 0.0026666667f

    /**
     * Unified scaling engine over the immutable metrics of the current resolution.
     * Callers that resolve through [getOrPut] receive a per-window snapshot; no result is
     * derived from a process-wide application configuration.
     */
    fun calculateRawScaling(
        baseValue: Float,
        applyAspectRatio: Boolean,
        customSensitivityK: Float?
    ): Float {
        require(baseValue.isFinite()) { "baseValue must be finite" }
        return baseValue * currentMetrics.scaledMultiplier(applyAspectRatio, customSensitivityK)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FAST SCALED PATH — single-multiply kernel for the dominant SDP case
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Ultra-fast resolution for the dominant SDP/SDPA path
     * (`SMALL_WIDTH` + `DEFAULT` inverter + no custom sensitivity).
     *
     * Resolves the coherent per-window [DimenMetrics] exactly like [getOrPut] does
     * ([metricsScope] → fast window identity → full rebuild) but then computes the
     * result with zero allocations, zero ThreadLocal writes, and zero cache-key
     * encoding: one branch + two float multiplies. Results are bit-identical to the
     * legacy math (`base * scale * density`).
     *
     * Per-window coherence is maintained by the event-driven config watcher
     * ([ensureConfigWatcher]): any real configuration change nulls the fast slot
     * synchronously, so the hit lane never samples the full configuration.
     *
     * PT Resolução ultra-rápida para o caminho SDP/SDPA dominante
     * (`SMALL_WIDTH` + inverter `DEFAULT` + sem sensibilidade customizada).
     */
    @PublishedApi
    internal inline fun resolveScaledFastPx(baseValue: Float, context: AppDimensContext?, qualifier: DpQualifier, applyAspectRatio: Boolean): Float {
        val m = metricsCoherentFor(context)
        return baseValue * fastScaledMultiplier(m, qualifier, applyAspectRatio) * m.density
    }

    @PublishedApi
    internal inline fun resolveScaledFastDp(baseValue: Float, context: AppDimensContext?, qualifier: DpQualifier, applyAspectRatio: Boolean): Float {
        val m = metricsCoherentFor(context)
        return baseValue * fastScaledMultiplier(m, qualifier, applyAspectRatio)
    }

    @PublishedApi
    internal inline fun metricsCoherentFor(context: AppDimensContext?): DimenMetrics {
        metricsScope?.let { return it }
        val slot = fastWindowSlot.load()
        if (slot !== EMPTY_FAST_WINDOW_SLOT && slot.context === context) return slot.metrics
        return metricsFor(context)
    }

    /**
     * EN Non-Compose fast-lane resolution: ThreadLocal-free.
     *
     * The Compose lane keeps [metricsScope] first so nested strategy calls inherit the
     * enclosing snapshot; code (non-Compose) lanes are never nested inside
     * [withMetrics], so the ThreadLocal probe is skipped entirely — one volatile load,
     * one identity compare, two float multiplies on the hit path.
     *
     * PT Resolução do fast lane não-Compose: sem ThreadLocal.
     */
    @PublishedApi
    internal inline fun fastMetricsForCode(context: AppDimensContext?): DimenMetrics {
        val slot = fastWindowSlot.load()
        if (slot !== EMPTY_FAST_WINDOW_SLOT && slot.context === context) return slot.metrics
        return metricsFor(context)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPECIALIZED KERNELS — one kernel per family/qualifier, zero branches.
    // Each multiplies in the exact legacy order (`base * factor * density`) so
    // every result is bit-identical to the 3.1.8 path. DP lanes omit the density
    // step (one multiply); PX lanes keep BOTH multiplies — pre-combining
    // `factor * density` would change rounding (IEEE-754 is not associative).
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Metrics-based fast kernels used by the Compose fast lane when the coherent
     *    per-window [DimenMetrics] is already at hand (from [metricsScope] or
     *    `LocalDimenMetrics`): one multiply set, no context chain, no atomic loads.
     *    Bit-identical to the context-based variants (same [fastScaledMultiplier]).
     * PT Kernels rápidos baseados em métricas usados pelo fast lane do Compose quando
     *    as [DimenMetrics] coerentes da janela já estão à mão (de [metricsScope] ou
     *    `LocalDimenMetrics`): um conjunto de multiplicações, sem cadeia de contexto,
     *    sem loads atômicos. Bit-idênticos às variantes baseadas em contexto.
     */
    @PublishedApi
    internal inline fun resolveScaledFastDpFromMetrics(
        baseValue: Float,
        m: DimenMetrics,
        qualifier: DpQualifier,
        applyAspectRatio: Boolean,
    ): Float = baseValue * fastScaledMultiplier(m, qualifier, applyAspectRatio)

    @PublishedApi
    internal inline fun resolveScaledFastPxFromMetrics(
        baseValue: Float,
        m: DimenMetrics,
        qualifier: DpQualifier,
        applyAspectRatio: Boolean,
    ): Float = baseValue * fastScaledMultiplier(m, qualifier, applyAspectRatio) * m.density

    /** SMALL_WIDTH, no AR → PX. @see resolveSdpDp */
    @PublishedApi
    internal inline fun resolveSdpPx(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.scale * m.density
    }

    /** SMALL_WIDTH, no AR → DP. */
    @PublishedApi
    internal inline fun resolveSdpDp(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.scale
    }

    /** SMALL_WIDTH + AR → PX. @see resolveSdpaDp */
    @PublishedApi
    internal inline fun resolveSdpaPx(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.defaultScaledAspectRatioMultiplier * m.density
    }

    /** SMALL_WIDTH + AR → DP. */
    @PublishedApi
    internal inline fun resolveSdpaDp(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.defaultScaledAspectRatioMultiplier
    }

    /** HEIGHT, no AR → PX. @see resolveHdpDp */
    @PublishedApi
    internal inline fun resolveHdpPx(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.screenHeightFactor * m.density
    }

    /** HEIGHT, no AR → DP. */
    @PublishedApi
    internal inline fun resolveHdpDp(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.screenHeightFactor
    }

    /** WIDTH, no AR → PX. @see resolveWdpDp */
    @PublishedApi
    internal inline fun resolveWdpPx(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.screenWidthFactor * m.density
    }

    /** WIDTH, no AR → DP. */
    @PublishedApi
    internal inline fun resolveWdpDp(baseValue: Float, context: AppDimensContext?): Float {
        val m = fastMetricsForCode(context)
        return baseValue * m.screenWidthFactor
    }

    /**
     * EN Public bridge used by satellite modules (separate Gradle modules cannot see
     *    `internal` members) to resolve the coherent per-window metrics for their fast
     *    lanes — same source as [metricsCoherentFor].
     * PT Ponte pública usada pelos módulos satélite (módulos Gradle separados não veem
     *    membros `internal`) para resolver as métricas coerentes por janela de seus
     *    fast lanes — mesma fonte de [metricsCoherentFor].
     */
    fun coherentMetrics(context: AppDimensContext?): DimenMetrics = metricsCoherentFor(context)

    /**
     * EN Public bridge for the feature toggle (internal in this module) so satellite
     *    fast lanes can replicate the exact cache-enabled semantics of their fallbacks.
     * PT Ponte pública para o toggle de recurso (internal neste módulo) para que os fast
     *    lanes dos satélites repliquem a semântica exata de cache habilitado dos fallbacks.
     */
    fun isScalingEnabled(): Boolean = isEnabled.load()

    /**
     * EN Single-multiply multiplier for SMALL_WIDTH / WIDTH / HEIGHT without custom
     *    sensitivity. AR is only offered for SMALL_WIDTH (other qualifiers with AR
     *    keep the slow path, which is mathematically identical but rarer).
     * PT Multiplicador de uma única multiplicação para SMALL_WIDTH / WIDTH / HEIGHT
     *    sem sensibilidade customizada. AR é oferecido apenas para SMALL_WIDTH.
     */
    @PublishedApi
    internal inline fun fastScaledMultiplier(
        m: DimenMetrics,
        qualifier: DpQualifier,
        applyAspectRatio: Boolean,
    ): Float {
        if (qualifier === DpQualifier.SMALL_WIDTH) {
            return if (applyAspectRatio) m.defaultScaledAspectRatioMultiplier else m.scale
        }
        // EN Identity compares instead of `when` on an enum: `when` compiles to an
        //    ordinal() virtual call + switch-table array load on every resolution.
        //    WIDTH/HEIGHT read precomputed factors (bit-identical, one load).
        // PT Comparações de identidade no lugar de `when` sobre enum; WIDTH/HEIGHT
        //    leem fatores pré-calculados (bit-idênticos).
        return if (qualifier === DpQualifier.WIDTH) {
            m.screenWidthFactor
        } else {
            m.screenHeightFactor
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PERSISTENCE FLOW — binary-compatibility stubs
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN No-op: dimension resolution no longer owns a background persistence scope.
     * PT No-op: a resolução de dimensões não possui mais escopo de persistência.
     */
    fun shutdown() = Unit

    // ─────────────────────────────────────────────────────────────────────────
    // KEY ENCODING
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Dimension type discriminator for the cache key.
     * PT Discriminador de tipo de dimensão para a chave de cache.
     */
    enum class ValueType {
        DP, PX, SP_WITH_SCALE, SP_NO_SCALE, SP_PX_WITH_SCALE, SP_PX_NO_SCALE
    }

    /**
     * Packs all dimension-calculation parameters into a single 64-bit [Long] key.
     *
     * Bit layout (MSB → LSB):
     * ```
     * [63]     applyAspectRatio          1 bit
     * [62-31]  baseValue bits            32 bits  (Float.toRawBits)
     * [30-27]  CalcType ordinal          4 bits  (covers 0..15)
     * [26-24]  ValueType                 3 bits  (covers 0..7)
     * [23-8]   sensitivityK fingerprint  16 bits (float bits ushr 16 & 0xFFFF)
     * [7-6]    DpQualifier ordinal       2 bits  (covers 0..3)
     * [5-2]    Inverter ordinal          4 bits  (covers 0..15)
     * [1]      isLandscape               1 bit
     * [0]      ignoreMultiWindows        1 bit
     * ```
     */
    fun buildKey(
        baseValue: Float,
        isLandscape: Boolean,
        ignoreMultiWindows: Boolean,
        calcType: CalcType,
        qualifier: DpQualifier,
        inverter: Inverter,
        applyAspectRatio: Boolean,
        valueType: ValueType,
        customSensitivityK: Float? = null
    ): Long {
        require(baseValue.isFinite()) { "baseValue must be finite" }
        require(customSensitivityK == null || customSensitivityK.isFinite()) {
            "customSensitivityK must be finite"
        }
        val ar  = if (applyAspectRatio) 1L else 0L
        val bv  = baseValue.toRawBits().toLong() and 0xFFFFFFFFL
        val ct  = calcType.ordinal.toLong() and 0xFL
        val vt  = valueType.ordinal.toLong() and 0x7L
        val sk  = (customSensitivityK?.toRawBits()?.ushr(16)?.and(0xFFFF)?.toLong() ?: 0xFFFFL)
        val q   = qualifier.ordinal.toLong() and 0x3L
        val inv = inverter.ordinal.toLong() and 0xFL
        // DIAGONAL / PERIMETER / DENSITY formulas use min/max or dpi — orientation-invariant.
        // Dropping the landscape bit avoids mandatory miss + duplicate slots on rotation.
        val land = when (calcType) {
            CalcType.DIAGONAL, CalcType.PERIMETER, CalcType.DENSITY -> 0L
            else -> if (isLandscape) 1L else 0L
        }
        val imw  = if (ignoreMultiWindows) 1L else 0L

        return (ar  shl 63) or
               (bv  shl 31) or
               (ct  shl 27) or
               (vt  shl 24) or
               (sk  shl  8) or
               (q   shl  6) or
               (inv shl  2) or
               (land shl 1) or
               imw
    }

    // Overload accepting Int baseValue (kept for call-site convenience)
    fun buildKey(
        baseValue: Int,
        isLandscape: Boolean,
        ignoreMultiWindows: Boolean,
        calcType: CalcType,
        qualifier: DpQualifier,
        inverter: Inverter,
        applyAspectRatio: Boolean,
        valueType: ValueType,
        customSensitivityK: Float? = null
    ): Long = buildKey(
        baseValue.toFloat(), isLandscape, ignoreMultiWindows, calcType,
        qualifier, inverter, applyAspectRatio, valueType, customSensitivityK
    )

    // ─────────────────────────────────────────────────────────────────────────
    // INIT / PERSISTENCE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Initializes the process-wide cache against the window described by [context].
     * Synchronous and local to the caller's window. A persisted result cache cannot be
     * made correct across a configuration, formula, density or multi-window change.
     *
     * PT Inicializa o cache global contra a janela descrita por [context].
     */
    fun init(context: AppDimensContext) {
        if (isInitializing.exchange(true)) return
        try {
            ensureConfigWatcher(context)
            val config = context.configuration
            updateFactors(config)
            lastConfiguration = ConfigSnapshot.from(config)
            isInitializedFast.store(true)
            isInitialized.store(true)
        } finally {
            isInitializing.store(false)
        }
    }

    /**
     * EN Compatibility no-op. The persistent result cache was removed in 3.1.8;
     * a serialized blob is never loaded or consulted.
     * PT No-op de compatibilidade. Nenhum blob é carregado ou consultado.
     */
    internal fun loadFromByteArray(data: ByteArray) = Unit

    fun saveToPersistence(context: AppDimensContext) {
        // Kept as a no-op. Result caching is intentionally in-memory and snapshot-scoped.
        Unit
    }

    /**
     * EN Stub returning an empty blob. Nothing is read back by this library.
     * PT Stub que retorna um blob vazio.
     */
    internal fun serializeToByteArray(): ByteArray = byteArrayOf(0, 0, 0, 0)

    // ─────────────────────────────────────────────────────────────────────────
    // FAST READ / WRITE (lock-free)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Compatibility entry point for callers that cannot use the public overload.
     * The context is converted to an immutable window snapshot before any cache lookup.
     */
    fun getOrPutInternal(key: Long, context: AppDimensContext?, compute: () -> Float): Float =
        resolve(key, metricsScope ?: metricsFor(context), compute)

    /**
     * EN Returns `true` when the packed [key] carries a custom sensitivity K. Only 16
     * bits of the 32-bit float fit in the key (`buildKey`), so two different K values
     * can alias the same key — a cached entry could then answer with the *other* K's
     * result. Custom-K calls therefore always compute exactly and never write the cache.
     *
     * PT Verdadeiro quando a [key] empacota um sensibilidade custom.
     */
    @PublishedApi
    internal fun hasCustomSensitivityKey(key: Long): Boolean =
        (key ushr 8 and 0xFFFFL).toInt() != 0xFFFF

    /**
     * EN Returns `true` when [getOrPut] should skip the snapshot cache and call `compute()`
     * directly. Covers simple no-AR multipliers **and** the default AR path where the
     * multiplier is already derived in the [DimenMetrics] snapshot — equally cheap as
     * a single multiply (~2 ns vs ~5 ns lookup).
     *
     * `CT_ASPECT_RATIO` (used by [getOrPutAspectRatio] / `fastLn`) is **not** bypassed;
     * since 3.1.8 that path computes the exact `ln()` once per snapshot (no memo table).
     *
     * PT Indica se o cache deve ser contornado (multiply barato, incl. AR padrão).
     */
    @PublishedApi
    internal fun shouldBypassCache(key: Long): Boolean {
        val ct = (key ushr 27 and 0xFL).toInt()
        val hasAr = (key ushr 63) != 0L
        val hasCustomK = hasCustomSensitivityKey(key)

        // Custom K only fits 16 bits in the key — never bypass; [resolve] computes it
        // exactly every time. distinct floats cannot alias.
        if (hasCustomK) return false

        val isAlwaysBypassType = ct == CT_PERCENT || ct == CT_SCALED || ct == CT_DENSITY ||
                ct == CT_DIAGONAL || ct == CT_INTERPOLATED || ct == CT_PERIMETER
        val isConditionalBypassType = ct == CT_POWER || ct == CT_LOGARITHMIC

        if (!isAlwaysBypassType && !isConditionalBypassType) return false

        val q = (key ushr 6 and 0x3L).toInt()
        val inv = (key ushr 2 and 0xFL).toInt()
        val isDefaultSwPath =
            q == DpQualifier.SMALL_WIDTH.ordinal && inv == Inverter.DEFAULT.ordinal

        if (isAlwaysBypassType) {
            // Default AR on SW is one precomputed multiply in [DimenMetrics]; non-default
            // qualifiers still need the full formula and must stay cacheable.
            return !hasAr || isDefaultSwPath
        }

        return isDefaultSwPath
    }

    /**
     * EN Resolves against the actual window configuration supplied by [context].  A lookup
     * never crosses window/configuration snapshots, so resizing, split-screen, density and
     * font-scale changes cannot return a cached value from an earlier environment.
     *
     * `inline` — the full hot path is inlined at each call-site, so the [compute] lambda
     * is not instantiated per call, while results remain partitioned per immutable
     * [DimenMetrics] snapshot.
     *
     * @param key      64-bit packed key from [buildKey]
     * @param compute  Lambda invoked only on a cache **miss**
     * @return         Cached or freshly-computed raw Float result
     */
    inline fun getOrPut(key: Long, context: AppDimensContext? = null, crossinline compute: () -> Float): Float =
        if (context != null) {
            resolve(key, metricsFor(context), compute)
        } else {
            resolve(key, metricsScope ?: fallbackMetrics.load(), compute)
        }

    /**
     * Explicit snapshot overload for callers that already hold the configuration used by
     * their formula (notably Compose providers and custom containers).
     */
    inline fun getOrPut(key: Long, metrics: DimenMetrics, crossinline compute: () -> Float): Float =
        resolve(key, metrics, compute)

    /**
     * Convenience overload preserving the exact [ScreenConfiguration] observed by a caller.
     */
    fun getOrPut(
        key: Long,
        screen: ScreenConfiguration,
        context: AppDimensContext? = null,
        compute: () -> Float,
    ): Float = resolve(
        key,
        DimenMetrics.from(screen, context?.isInMultiWindowMode == true),
        compute,
    )

    /** Backward compatibility for non-context calls. */
    fun getOrPut(key: Long, compute: () -> Float): Float =
        getOrPut(key, null, compute)

    /**
     * EN Reads a stored cache value without computing a fallback. Returns `null` on a miss.
     *
     * **Bypass interaction:** [getOrPut] intentionally **does not write** to the snapshot cache
     * for certain cheap calculation types when aspect ratio is off (see fast-path bypass in
     * [getOrPut]). For those keys, [peek] will typically return `null` even after [getOrPut]
     * returned a value — the result was computed but not persisted. Use [getOrPut] when you
     * need the resolved float; use [peek] only to probe entries that were actually stored.
     *
     * PT Lê um valor gravado no cache sem calcular fallback. Retorna `null` em miss.
     */
    fun peek(key: Long): Float? = peek(key, fallbackMetrics.load())

    /** Reads an entry from the partition matching [context]'s current window snapshot. */
    fun peek(key: Long, context: AppDimensContext?): Float? = peek(key, metricsFor(context))

    /** Reads an entry from one explicit metrics partition. */
    fun peek(key: Long, metrics: DimenMetrics): Float? {
        if (!isEnabled.load()) return null
        val cache = locked(snapshotCacheLock) { snapshotCaches[metrics] } ?: return null
        val entry = cache.entries.loadAt(slotFor(key))
        return if (entry.valueBits != EMPTY_ENTRY.valueBits && entry.key == key) {
            Float.fromBits(entry.valueBits)
        } else {
            null
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC BATCH API
    //
    // getBatch() was previously internal. Exposing it as a public function
    // allows callers (e.g. RecyclerView adapters, LazyColumn producers)
    // to resolve N dimensions inside a single tight loop. The JIT can then
    // auto-vectorize the inner computation loop (4-wide NEON on ARM64).
    //
    // Usage:
    //   val keys = LongArray(items.size) { i -> DimenCache.buildKey(items[i], ...) }
    //   val results = DimenCache.getBatch(keys, context) { i -> computeItem(i) }
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN SIMD-friendly batch resolution.
     *
     * Resolves [keys].size cache entries in a single tight loop. On a cache miss, the
     * provided [compute] lambda is called with the index; the result is stored and returned.
     * The loop structure is intentionally simple to help the ART JIT emit vectorized
     * (NEON) instructions for the computation body when all items compute the same formula.
     *
     * This is a **public** API — callers outside the library can use it to batch-resolve
     * any set of pre-built keys.
     *
     * PT Resolução em lote amigável ao SIMD / JIT auto-vetorização.
     * API pública — pode ser chamada por código fora da biblioteca.
     *
     * @param keys    Array of 64-bit keys built via [buildKey]
     * @param context Optional context used to derive the window snapshot partition
     * @param compute Lambda `(index: Int) -> Float` called on cache miss
     * @return        [FloatArray] of resolved values in the same order as [keys]
     */
    fun getBatch(
        keys: LongArray,
        context: AppDimensContext? = null,
        compute: (Int) -> Float
    ): FloatArray = getBatch(keys, FloatArray(keys.size), context, compute)

    /**
     * EN Zero-allocation batch resolution: writes into [destination] (which must be at
     * least [keys].size long and is returned as-is), so list adapters / producers can
     * reuse one buffer across frames instead of allocating a [FloatArray] per call.
     *
     * PT Resolução em lote sem alocação: grava em [destination].
     */
    fun getBatch(
        keys: LongArray,
        destination: FloatArray,
        context: AppDimensContext? = null,
        compute: (Int) -> Float
    ): FloatArray {
        val size = keys.size
        require(destination.size >= size) {
            "destination must hold at least ${keys.size} values (got ${destination.size})"
        }
        // Resolve the environment once. A batch is atomic with respect to the window
        // snapshot even if a resize arrives while the caller is iterating.
        val metrics = metricsScope ?: metricsFor(context)
        for (i in 0 until size) {
            destination[i] = resolve(keys[i], metrics) { compute(i) }
        }
        return destination
    }

    fun getOrPutAspectRatio(normalizedAr: Float, context: AppDimensContext? = null): Float {
        require(normalizedAr.isFinite() && normalizedAr > 0f) {
            "normalizedAr must be a positive, finite value"
        }
        // This is executed only while creating a DimenMetrics snapshot. Exact math here
        // avoids a lossy global lookup table and does not burden a frame-time hot path.
        return ln(normalizedAr.toDouble()).toFloat()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INVALIDATION
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Selectively invalidates the cache based on what actually changed.
     * PT Invalida seletivamente o cache baseado no que mudou.
     */
    fun invalidateOnConfigChange(new: ScreenConfiguration) {
        // A real configuration change invalidates the fast identity slots so the
        // next resolution rebuilds the snapshot (event-driven coherence — see
        // [ensureConfigWatcher]).
        fastWindowSlot.store(EMPTY_FAST_WINDOW_SLOT)
        fastMwSlot.store(EMPTY_MW_SLOT)
        lastConfiguration = ConfigSnapshot.from(new)
        updateFactors(new)
        // Snapshot partitions make explicit invalidation unnecessary for correctness.
        // Keep this API as a compatibility hook, but do not erase other windows' hot
        // entries whenever one window rotates or is resized.
    }

    private fun updateFactors(screen: ScreenConfiguration) {
        fallbackMetrics.store(DimenMetrics.from(screen))
        val metrics = sharedMetricsFrom(screen)
        val f = factors

        f.scale = metrics.scale
        f.normalizedAr = metrics.normalizedAr
        f.logNormalizedAr = metrics.logNormalizedAr
        f.arMultiplier = metrics.arMultiplier
        f.density = metrics.density
        f.aspectRatioMul = metrics.aspectRatioMul
        f.smallestWidthDp = metrics.smallestWidthDp.toInt()

        // `factors` remains populated only for binary/source compatibility. Production
        // formulas resolve through currentMetrics, so no process-global strategy update is
        // published here.
    }

    /** EN Clears all cache slots. Java-compatible alias. */
    fun clear(context: AppDimensContext? = null) = clearAll(context)

    // ─────────────────────────────────────────────────────────────────────────
    // clearAll / CLEAR
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * EN Clears all snapshot partitions. Thread-safe: an in-flight resolver may finish
     * on an old partition, but it can never publish into the new cache after the clear.
     *
     * PT Limpa todas as partições de snapshot. Thread-safe.
     */
    fun clearAll(context: AppDimensContext? = null) {
        // Detaching whole partitions is atomic from the perspective of future lookups:
        // an in-flight resolver may finish on an old partition, but it can never publish
        // into the new cache after the clear.
        locked(snapshotCacheLock) { snapshotCaches.clear() }
        fastPartition.store(EMPTY_PARTITION)
        fastPartitionMetrics.store(DimenMetrics.DEFAULT)
        fastWindowSlot.store(EMPTY_FAST_WINDOW_SLOT)
        fastMwSlot.store(EMPTY_MW_SLOT)
        notifyResetListeners()
    }

    /**
     * EN Clears only cache entries whose [ValueType] embeds fontScale
     * (`SP_NO_SCALE`, `SP_PX_WITH_SCALE`, `SP_PX_NO_SCALE`). Leaves DP/PX/`SP_WITH_SCALE`.
     *
     * PT Limpa só entradas cujo ValueType embute fontScale.
     */
    internal fun clearFontScaleDependentEntries() {
        // Font scale is part of DimenMetrics equality. Existing entries therefore cannot
        // be read by a new font-scale snapshot. Dropping old partitions is bounded and
        // safer than decoding a partial legacy key format.
        locked(snapshotCacheLock) { snapshotCaches.clear() }
        notifyResetListeners()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIAGNOSTICS
    // ─────────────────────────────────────────────────────────────────────────

    fun stats(): CacheStats {
        var populated = 0
        val caches = locked(snapshotCacheLock) { snapshotCaches.values.toList() }
        for (cache in caches) {
            for (i in 0 until SNAPSHOT_CACHE_SIZE) {
                if (cache.entries.loadAt(i).valueBits != EMPTY_ENTRY.valueBits) populated++
            }
        }
        val hits   = hitCount.load()
        val misses = missCount.load()
        val total  = hits + misses
        val capacity = caches.size * SNAPSHOT_CACHE_SIZE
        return CacheStats(
            capacity   = capacity,
            populated  = populated,
            fillRatio  = if (capacity > 0) populated.toFloat() / capacity else 0f,
            hits       = hits,
            misses     = misses,
            evictions  = evictionCount.load(),
            hitRate    = if (total > 0) hits.toFloat() / total else 0f
        )
    }

    /**
     * EN Resets the diagnostic counters (hit, miss, eviction) to zero.
     * PT Zera os contadores de diagnóstico (hit, miss, eviction).
     */
    fun resetDiagnostics() {
        hitCount.store(0L)
        missCount.store(0L)
        evictionCount.store(0L)
    }

    /**
     * EN Cache usage statistics snapshot. The [hits], [misses], [evictions], and [hitRate]
     * fields are only meaningful when [diagnosticsEnabled] is `true`.
     *
     * PT Snapshot de métricas de uso do cache. [hits], [misses], [evictions] e [hitRate]
     * só são significativos quando [diagnosticsEnabled] está `true`.
     */
    data class CacheStats(
        val capacity  : Int,
        val populated : Int,
        val fillRatio : Float,
        val hits      : Long = 0,
        val misses    : Long = 0,
        val evictions : Long = 0,
        val hitRate   : Float = 0f
    ) {
        override fun toString(): String =
            "DimenCache: $populated/$capacity slots used (${(fillRatio * 100).toInt()}% fill)" +
            if (hits + misses > 0) ", hits=$hits misses=$misses evictions=$evictions hitRate=${(hitRate * 100).toInt()}%" else ""
    }
}