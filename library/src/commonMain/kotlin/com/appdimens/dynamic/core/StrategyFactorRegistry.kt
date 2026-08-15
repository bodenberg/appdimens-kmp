/**
 * EN Strategy factor registry — satellites register config-change callbacks so the
 * core [DimenCache] never pre-computes scales for strategies absent from the classpath.
 *
 * PT Registry de fatores: satélites registram callbacks; o core não pré-calcula
 * escalas de estratégias que não estão no APK.
 *
 * KMP: synchronized list instead of CopyOnWriteArrayList (which is JVM-only).
 */
package com.appdimens.dynamic.core

/**
 * EN Shared screen metrics computed once in [DimenCache] on configuration change.
 * PT Métricas compartilhadas calculadas uma vez no core em mudança de configuração.
 */
data class SharedScreenMetrics(
    val smallestWidthDp: Float,
    val minDimDp: Float,
    val maxDimDp: Float,
    val density: Float,
    val scale: Float,
    val normalizedAr: Float,
    val logNormalizedAr: Float,
    val arMultiplier: Float,
    val aspectRatioMul: Float,
)

fun interface StrategyFactorContributor {
    fun onScreenFactorsUpdated(metrics: SharedScreenMetrics)
}

/**
 * EN Process-wide registry. [register] is idempotent per instance and immediately
 * replays the last metrics so late class-loading still receives current scales.
 *
 * PT Registry de processo; [register] reaplica as últimas métricas (class-load tardio).
 */
object StrategyFactorRegistry {
    private val contributors = mutableListOf<StrategyFactorContributor>()
    private val contributorsLock = kotlinx.atomicfu.locks.SynchronizedObject()

    private var lastMetrics: SharedScreenMetrics? = null

    fun register(contributor: StrategyFactorContributor) {
        locked(contributorsLock) {
            if (!contributors.contains(contributor)) {
                contributors.add(contributor)
            }
            lastMetrics?.let { contributor.onScreenFactorsUpdated(it) }
        }
    }

    fun unregister(contributor: StrategyFactorContributor) {
        locked(contributorsLock) {
            contributors.remove(contributor)
        }
    }

    internal fun publish(metrics: SharedScreenMetrics) {
        lastMetrics = metrics
        val snapshot = locked(contributorsLock) { contributors.toList() }
        for (contributor in snapshot) {
            contributor.onScreenFactorsUpdated(metrics)
        }
    }

    /** EN Test helper — clears contributors and last metrics. PT Auxiliar de teste. */
    internal fun resetForTest() {
        locked(contributorsLock) {
            contributors.clear()
            lastMetrics = null
        }
    }

    internal fun lastMetricsForTest(): SharedScreenMetrics? = lastMetrics
}

/**
 * EN Builds [SharedScreenMetrics] from a [ScreenConfiguration] using the same formulas as
 * the former monolithic [DimenCache] shared path (scale / AR / density).
 *
 * PT Constrói métricas compartilhadas a partir de [ScreenConfiguration].
 */
internal fun sharedMetricsFrom(screen: ScreenConfiguration): SharedScreenMetrics {
    val sw = screen.smallestScreenWidthDp.toFloat()
    val maxDim = maxOf(screen.screenWidthDp.toFloat(), screen.screenHeightDp.toFloat())
    val minDim = minOf(screen.screenWidthDp.toFloat(), screen.screenHeightDp.toFloat())
    val scale = sw * DimenCache.INV_BASE_RATIO
    val rawAr = if (minDim > 0) maxDim / minDim else 1.0f
    val normalizedAr = rawAr / 1.78f
    val logNormalizedAr = fastLn(normalizedAr)
    val diff = sw - 300f
    val adjustment = DimenCache.SENSITIVITY_DEFAULT * logNormalizedAr
    val arMultiplier = 1.0f + diff * (DimenCache.ADJUSTMENT_SCALE + adjustment)
    val density = screen.densityDpi.toFloat() / 160f
    val aspectRatioMul = 1f + DimenCache.SENSITIVITY_DEFAULT * logNormalizedAr
    return SharedScreenMetrics(
        smallestWidthDp = sw,
        minDimDp = minDim,
        maxDimDp = maxDim,
        density = density,
        scale = scale,
        normalizedAr = normalizedAr,
        logNormalizedAr = logNormalizedAr,
        arMultiplier = arMultiplier,
        aspectRatioMul = aspectRatioMul,
    )
}