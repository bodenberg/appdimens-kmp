# AppDimens Dynamic documentation — strategies by package

This folder goes deeper into each **scaling strategy** in [AppDimens Dynamic](../README.md): what it is, the formula, how to import it, and when to pick each mode. Each strategy’s code lives in `com.appdimens.kmp.compose.<strategy>` and `com.appdimens.kmp.code.<strategy>` with **no cross-imports** between strategies.

**Modules (1.0.1):** principal `appdimens-kmp` (scaled + core/common/plain); strategy modules `appdimens-kmp-<strategy>`; BOM `appdimens-kmp-bom`. See [README — Installation](../README.md#installation-v101) · [MODULES.md](MODULES.md).

### 1.0.0 Changes (KMP port)

- **Full Kotlin Multiplatform port**: same packages, API and math on Android, JVM, iOS (`iosArm64`/`iosSimulatorArm64`), macOS (`macosArm64`), Kotlin/JS and wasmJs (browser).
- **Platform-neutral `AppDimensContext`**: non-Compose APIs take a window handle instead of an Android `Context`; per-platform adapters (Android `Context`, AWT window, `UIScreen`, `NSScreen`, browser viewport).
- **Event-driven config watcher**: Android keeps the `ComponentCallbacks2` listener; desktop/web/iOS/macOS rebuild the snapshot on live window configuration (resize-aware on every platform).
- **Specialized kernels**: Zero-branch resolution per family/qualifier (`resolveSdpPx`, `resolveSdpaPx`, etc.).
- **Non-Compose fast lane**: `fastMetricsForCode` skips ThreadLocal probe.
- **Compose fast lane**: `metricsScope ?: LocalDimenMetrics` — one CompositionLocal read + one multiply on every platform.
- **DimenMetrics eager AR**: `normalizedAspectRatio` / `logNormalizedAspectRatio` changed from `lazy` to plain `val`.
- **R8 pre-shrink for all AARs**: all 14 Android modules ship R8-optimized bytecode (`optimization { minify = true }`).
- **BenchLab KMP**: competitor benchmark (Dynamic vs SDPS vs Lib #2) that runs on Android, JVM, iOS, macOS and the browser.

### 1.0.1 Changes (audit fixes + full target matrix)

- **`fastPartition` race fixed**: the fast partition is now a single atomic `FastPartitionSlot(metrics, partition)` — before, two independent atomics could pair a snapshot partition with another window's metrics and return a wrong dimension under concurrency.
- **Android context cache cycle fixed**: the `WeakHashMap<Context, …>` value no longer holds the key strongly (now a `WeakReference`), so Activities/Contexts are collectable.
- **Configuration listeners disposable**: `registerConfigurationListener` returns `ConfigurationRegistration` with `dispose()`; the Android registry unregisters `ComponentCallbacks` when the last listener is removed.
- **Config-watcher lifecycle hardened**: the registered listener is now **context-free** (no value→key cycle), the watcher is **reference-counted** (`acquireConfigWatcher` / `releaseConfigWatcher`), the Android `AppDimensProvider` pairs them via `DisposableEffect`, and the Android registry holds listeners **weakly** — a destroyed Activity is collectable even without an explicit `dispose()`.
- **Weak identity map fixed (JVM/Android)**: `WeakHashMap<WeakKey<K>, V>` made the weak wrapper the map's weak key, so live entries could be dropped while the original key was strongly reachable. Replaced with a strong `HashMap` of `IdentityWeakReference` wrappers + `ReferenceQueue`.
- **Native metrics scope is thread-local**: the Kotlin/Native `MetricsScopeHolder` is `@ThreadLocal` — each worker gets its own slot (was one shared mutable global).
- **Native/Web identity maps use `===`**: `SynchronizedIdentityMap` / `WebIdentityMap` compare window handles by identity instead of `equals()`.
- **Strict race tests**: `DimenCacheRaceTest` requires the exact expected value per key/snapshot — no more false negatives. New GC / identity / lifecycle / native-worker tests added.
- **New targets**: classic Kotlin/JS (`js`, IR), `linuxX64`, `linuxArm64`, `mingwX64` on every module via the shared `appdimens.kmp-library` convention plugin. Linux/Windows expose the `code` API only.
- **Encapsulated diagnostics**: `DimenCache.isInitialized` is a plain `Boolean` and `cacheStats()` returns immutable `CacheStats`; experimental atomics are `internal`.
- **CI restored**: `verify-linux` + `verify-apple` gates; wrapper `distributionSha256Sum` pinned; Compose dev repository removed from the resolution path.

**Product docs:** [PRD.md](PRD.md) · [PDR.md](PDR.md) · [MATHEMATICS-AND-CALCULUS.md](MATHEMATICS-AND-CALCULUS.md).

For **cache, bypass, and performance**, see also [library/PERFORMANCE.md](../library/PERFORMANCE.md).

**Naming parity (`compose` vs `code`):** In the multi-module tree, each strategy lives under **`library/`** (scaled) or **`library-<strategy>/`**, pairing **`Dimen<Strategy>DpExtensions.kt`** (layout facilitators → `Float` px + `AppDimensContext`) with **`Dimen<Strategy>SpExtensions.kt`** where Sp facilitators exist — the same filenames as under `compose/<strategy>/`, so it is easy to jump between UI toolkits. **Scaled** uses **`DimenSdpExtensions.kt`** and **`DimenSspExtensions.kt`** inside the `scaled/` subfolder (packages stay top-level `compose` / `code`). **Plain** helpers remain in **`Dimen<Strategy>PlainPx.kt`** per strategy plus shared logic in **`com.appdimens.kmp.code.plain`** (principal artifact).

**Compose API catalog:** [COMPOSE-API-CONVENTIONS.md](COMPOSE-API-CONVENTIONS.md) (scaled surface + prefix map; §4.5 View/`code` Plain). Resize: [resize.md](resize.md).

**Package index:** [index.md](index.md).

**Note:** Dokka HTML export can be generated per module (`./gradlew :library:dokkaGenerateHtml`); the committed docs are the strategy guides and this index.

## Summary

| Strategy | Maven artifact (1.0.1) | Document |
|----------|------------------------|----------|
| **Unified math (all strategies)** | — | [MATHEMATICS-AND-CALCULUS.md](MATHEMATICS-AND-CALCULUS.md) |
| **Module graph / packaging** | see [MODULES.md](MODULES.md) | [MODULES.md](MODULES.md) |
| **BOM (version alignment only)** | `appdimens-kmp-bom` | [MODULES.md](MODULES.md) |
| Scaled (default SDP / HDP / WDP) | `appdimens-kmp` (principal) | [scaled.md](scaled.md) |
| Percent (linear 1/300 + `space*`) | `appdimens-kmp-percent` | [percent.md](percent.md) |
| Power (sublinear) | `appdimens-kmp-power` | [power.md](power.md) |
| Fluid (320–768 dp band) | `appdimens-kmp-fluid` | [fluid.md](fluid.md) |
| Auto (linear + log after 480 dp) | `appdimens-kmp-auto` | [auto.md](auto.md) |
| Diagonal | `appdimens-kmp-diagonal` | [diagonal.md](diagonal.md) |
| Fill (“cover”) | `appdimens-kmp-fill` | [fill.md](fill.md) |
| Fit (“contain”) | `appdimens-kmp-fit` | [fit.md](fit.md) |
| Interpolated | `appdimens-kmp-interpolated` | [interpolated.md](interpolated.md) |
| Logarithmic | `appdimens-kmp-logarithmic` | [logarithmic.md](logarithmic.md) |
| Perimeter | `appdimens-kmp-perimeter` | [perimeter.md](perimeter.md) |
| Density | `appdimens-kmp-density` | [density.md](density.md) |
| Resize (constraint-based auto-fit) | `appdimens-kmp-resize` | [resize.md](resize.md) |
| Physical units (mm, cm, in) | `appdimens-kmp-units` | [physical-units.md](physical-units.md) |

### Quick links

- [PRD.md](PRD.md) · [PDR.md](PDR.md)

0. [KDoc API — root index](index.md)  
0a. [Modules — Maven/Gradle graph (1.0.1)](MODULES.md)  
0b. [Mathematics & calculus — formal reference](MATHEMATICS-AND-CALCULUS.md)  
1. [Compose API reference — conventions & scaled catalog](COMPOSE-API-CONVENTIONS.md)  
2. [Scaled](scaled.md) — recommended starting point  
3. [Percent](percent.md)  
4. [Power](power.md)  
5. [Fluid](fluid.md)  
6. [Auto](auto.md)  
7. [Diagonal](diagonal.md)  
8. [Fill](fill.md)  
9. [Fit](fit.md)  
10. [Interpolated](interpolated.md)  
11. [Logarithmic](logarithmic.md)  
12. [Perimeter](perimeter.md)  
13. [Density](density.md)  
14. [Resize](resize.md)  
15. [Physical units](physical-units.md)  
16. [Mathematics & calculus](MATHEMATICS-AND-CALCULUS.md)  

## Suggested decision flow

```mermaid
flowchart LR
  start[New_layout]
  scaled[scaled_sdp_hdp_wdp]
  qa[QA_phone_tablet]
  other[Other_strategy]
  start --> scaled
  scaled --> qa
  qa -->|curve_not_right| other
```

Always start with **scaled**; switch strategy only where visual QA or requirements (TV, ultrawide, split-screen) need a different growth curve.
