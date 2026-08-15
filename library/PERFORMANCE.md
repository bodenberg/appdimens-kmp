# AppDimens Dynamic KMP — Cache, bypass and performance notes (library)

This page documents the **implementation-level** performance architecture of the KMP library: the snapshot-partitioned cache, the fast bypass layer, and the platform-neutral fast lanes. For measured numbers see [PERFORMANCE.md](../PERFORMANCE.md) and [PERFORMANCE-COMPARATIVE.md](../PERFORMANCE-COMPARATIVE.md).

## Snapshot-partitioned cache

- **`DimenMetrics`** is the immutable per-window snapshot: `screenWidthDp`, `screenHeightDp`, `smallestScreenWidthDp`, `densityDpi`, `fontScale` (as raw bits), `orientation`, `uiMode`, `isInMultiWindowMode`.
- Each snapshot owns a bounded `AtomicReferenceArray` partition. Entries are published as single atomic `CacheEntry` references — a resolution for window A can never read a value computed for window B.
- **Keys** are 64-bit packed values built by `DimenCache.buildKey` from `(baseValue, landscape, ignoreMultiWindows, calcType, qualifier, inverter, applyAspectRatio, valueType, customSensitivityK)`. `CalcType` ordinals live in core so keys stay stable across modules.
- Since **1.0.0** there is **no disk persistence** — the cache is in-memory and partitioned per window snapshot; rotation/resize/recreation can never serve a stale value. `saveToPersistence` / `loadFromByteArray` / `serializeToByteArray` are removed.

## Fast bypass layer

For eligible `CalcType`s on the default path, `getOrPut` returns `compute()` without touching the snapshot cache — typically `baseValue × precomputedFactor` (~2 ns), which is faster than the fastest cache lookup (~5 ns):

| Path | Cost | Cache used? |
|:---|:---:|:---:|
| SCALED / default (most common) | ~2 ns | ❌ Bypass |
| SCALED / custom sensitivity or non-default qualifier | varies | ✅ Cache |
| POWER / LOG on SW+DEFAULT | ~2 ns | ❌ Bypass |
| AUTO / FLUID / FIT / FILL | lookup + compute | ✅ Cache |

## Fast lanes

**Compose (all platforms):** `toDynamicScaledDp` / `toDynamicScaledPx` read `DimenCache.metricsScope ?: LocalDimenMetrics` when the guard passes (`inverter == DEFAULT && !ignoreMultiWindows && customSensitivityK == null && (qualifier == SMALL_WIDTH || !applyAspectRatio)`) — **one CompositionLocal read + one float multiply**, resize-aware on every platform. The fallback goes through `resolveScaledFastDp/Px` (context chain → fast window slot).

**Code (non-Compose):** `Float.toDynamicScaledPx` / `toDynamicScaledDp` route the same guard to the **specialized kernels** `resolveSdpPx` / `resolveSdpaPx` / `resolveHdpPx` / `resolveWdpPx` (and Dp twins) — zero branches, volatile load + identity compare against the fast window slot + the legacy multiply order. `fastMetricsForCode` skips the ThreadLocal probe entirely.

**Zero allocations:** the fast lanes allocate nothing — no key encoding, no `remember` machinery, no ThreadLocal writes. `DimenMetrics` eager AR (`normalizedAspectRatio` / `logNormalizedAspectRatio` as plain `val`) removes the hidden `synchronized` probe from the SDPA path.

## Multi-window & coherence

- `DimenCalculationPlumbing.isMultiWindowConstrained` detects split-screen; `ignoreMultiWindows` (`i` suffix) returns the raw base value when the heuristic triggers.
- On Android a `ComponentCallbacks2` listener registered on the Application invalidates fast slots **synchronously** on any real configuration change.
- On desktop/web/iOS/macOS `AppDimensProvider` builds the context from the **live window configuration** (`remember(configuration)`): a resize creates a new snapshot identity → fast-slot miss → rebuild. `registerConfigurationListener` is a no-op outside Android; the identity-based rebuild covers the same guarantee.

## R8 notes (Android target)

- AARs are pre-shrunk/optimized at build time (`optimization { minify = true }` + `-optimizationpasses 10` + `-allowaccessmodification`, `-dontobfuscate`).
- `consumer-rules.pro` keeps: public API `-keepnames`, `kotlin.Metadata`, cache-key enums (ordinals packed into keys), the `ResizeBound` sealed hierarchy, and the `ScreenFactors` ARM64 false-sharing padding fields. See [R8-PROGUARD.md](../R8-PROGUARD.md).

---

*AppDimens Dynamic KMP 1.0.0 — library performance notes.*
