# Changelog

All notable changes to **AppDimens Dynamic KMP** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.1] — 2026-08-15

### Fixed (audit findings from the 1.0.0 release)

- **`fastPartition` race condition (release blocker).** The fast partition was published through two independent atomics (`fastPartition` + `fastPartitionMetrics`); a valid thread interleaving could pair one window/snapshot's partition with another window's metrics and return a wrong dimension under concurrency. Metrics and partition are now published as **one** atomic `FastPartitionSlot(metrics, partition)` — the impossible `partition(B) + metrics(A)` state is eliminated.
- **Android context cache cycle.** `WeakHashMap<Context, AndroidAppDimensContext>` was neutralized because the value held the key strongly, keeping Activities/Contexts alive indefinitely. The value is now a `WeakReference<AndroidAppDimensContext>`, so discarded Activities are collectable again.
- **Configuration listeners never unregistered.** `registerConfigurationListener` now returns a `ConfigurationRegistration` with `dispose()`; the Android registry unregisters `ComponentCallbacks` and drops per-app listener sets when the last listener is removed — no more listener/context accumulation.
- **Weak race-test assertions (false negatives).** `DimenCacheRaceTest` now requires the **exact expected value** per key/snapshot (it no longer accepts “any valid value” from another key), and a wrong transient read is counted even if a later `peek()` was corrected by another thread.
- **`WeakIdentityHashMap` structural defect (JVM/Android).** The previous `WeakHashMap<WeakKey<K>, V>` made the weak *wrapper* the WeakHashMap's weak key: nothing strongly referenced the wrapper after insertion, so the GC could drop live entries while the original key was still strongly reachable (lost memoization, re-registered listeners, lost `dispose()` handles). Replaced with a **strong `HashMap` of `IdentityWeakReference` wrappers + `ReferenceQueue`** — entries now live exactly as long as their referent and are drained deterministically.
- **Native `MetricsScopeHolder` not thread-local.** The Kotlin/Native actual was a single shared mutable `var` in a global object — two workers resolving concurrently could observe each other's `current` (same cross-snapshot contamination class the `FastPartitionSlot` fix eliminated on JVM). The holder is now **`@ThreadLocal`**: every worker gets its own instance.
- **Native/Web “identity” maps used `equals()`.** `SynchronizedIdentityMap` and `WebIdentityMap` were backed by `LinkedHashMap`, which compares with `equals()` — two distinct but equal window handles collapsed into one key. Both now compare with **`===`** (identity), matching the JVM/Android semantics.
- **Configuration-listener lifecycle still could retain an Activity.** The registered listener captured the window context (`{ onContextConfigChanged(context) }`), forming a value→key cycle in the weak watcher map; and `remember` in the Compose provider never disposed the registration. The listener is now **context-free** (a real config change invalidates the global fast slots; the next `metricsFor` rebuilds from the live context), the watcher is **reference-counted** (`acquireConfigWatcher` / `releaseConfigWatcher`), the Android `AppDimensProvider` acquires on composition and releases via `DisposableEffect`, and the Android registry holds listeners **weakly** (pruning dead references) — a destroyed Activity is collectable even without an explicit `dispose()`.
- **Apple CI referenced a nonexistent task.** `verify-apple.yml` ran `:library:linkDebugFrameworkIosSimulatorArm64`, but this library publishes KMP Maven variants (no `binaries.framework` configured), so the task does not exist and the gate could never be green. The link step was removed — the compile steps remain the correct Apple gates.

### Added

- **Full KMP target matrix.** Added classic **Kotlin/JS** (`js`, IR), **Linux** (`linuxX64` + `linuxArm64`) and **Windows** (`mingwX64`) targets on every library module, driven by the shared **`appdimens.kmp-library`** convention plugin (`build-logic`) — no more duplicated target setup across modules. Linux/Windows expose the `code` API only (`defaultPlatformContext() = null`; build an `AppDimensContext` from a `ScreenConfiguration` or use the `DimenMetrics` overloads).
- **Restored CI gates.** `verify-linux` (Linux + JVM + JS + wasm tests on ubuntu) and `verify-apple` (macOS runner compiling `iosSimulatorArm64` / `macosArm64`) re-added as release gates.

### Changed

- **Encapsulated diagnostics API.** `DimenCache.isInitialized` is now a plain `Boolean` and `cacheStats()` returns an immutable `CacheStats` (`hits` / `misses` / `evictions`); the experimental atomics are `internal`, keeping the public ABI clean.
- **Distribution hardening.** Gradle wrapper pins `distributionSha256Sum`; the Compose dev repository was removed from `settings.gradle.kts`; `LICENSE` (Apache-2.0) and signed Maven publishing (with sources) are configured; BOM coordinates derive from the shared `appdimens.version` property.
- **wasmJs browser tests selectable.** `-Pappdimens.wasmTestBrowser=firefox|chrome|safari` (default `chrome`) on the convention plugin.
- **BenchLab headless automation.** `AUTO_START` now auto-exports the report on completion and guards against overlapping runs.

### Added

- **GC / identity / lifecycle tests.** `WeakIdentityMapGcTest` (JVM: entry survives GC while the key is strongly reachable; entry disappears after the key dies), `WeakIdentityMapSemanticsTest` (all targets: `===` identity, bounded eviction), `ConfigWatcherLifecycleTest` (all targets: reference-counted acquire/release, dispose, re-register) and `MetricsScopeHolderNativeTest` (Kotlin/Native: two workers never cross `current`; nested `withMetrics` restores the outer value).

### Verified

- `assemble` for all 19 modules on all configured targets — ✅
- Tests: JVM (104), Kotlin/JS (99), linuxX64 (88), wasmJs/Firefox (99), Android host (93) — 0 failures
- Compile: `mingwX64` (Windows) + `linuxArm64` + iOS (`iosArm64`/`iosSimulatorArm64`) + macOS (`macosArm64`) — ✅
- BenchLab desktop (release, real window): **Dynamic 3.3 ns/op vs Lib #2 342 ns/op — ×95–104 faster**, anti-DCE checksums identical (bit-identical precision)
- BenchLab on device (Xiaomi, release, R8): **Dynamic 23.9 ns/op vs Lib #2 1.3 µs/op — ×54 faster**, values 1:1
- Formulas validated bit-identical against the official `appdimens-dynamic` Android library

---

## [1.0.0] — 2026-08-15

First stable Kotlin Multiplatform release.

### Added

- **Full KMP port** — same packages/API/math as the Android original (`appdimens-kmp`), compiled for Android, JVM, iOS (`iosArm64`/`iosSimulatorArm64`), macOS (`macosArm64`) and wasmJs (browser).
- **Platform-neutral `AppDimensContext`** — non-Compose APIs take a window handle instead of an Android `Context`; per-platform adapters (Android `Context`, AWT window, `UIScreen`, `NSScreen`, browser viewport).
- **Event-driven config watcher** — Android keeps the `ComponentCallbacks2` listener; desktop/web/iOS/macOS rebuild the snapshot on live window configuration (resize-aware on every platform).
- **Specialized kernels** — `resolveSdpPx`, `resolveSdpDp`, `resolveSdpaPx`, `resolveSdpaDp`, `resolveHdpPx`, `resolveHdpDp`, `resolveWdpPx`, `resolveWdpDp` — one kernel per family/qualifier, zero branches, volatile load + identity compare + legacy multiply order.
- **`fastMetricsForCode`** — non-Compose fast-lane resolution: skips the ThreadLocal probe (one volatile load, one identity compare, two float multiplies on hit).
- **Compose fast lane** — reads `metricsScope ?: LocalDimenMetrics` (one CompositionLocal + one multiply), no `LocalContext`.
- **DimenMetrics eager AR** — `normalizedAspectRatio` / `logNormalizedAspectRatio` are plain `val` (no hidden `synchronized` probe in the SDPA fast lane).
- **R8 pre-shrink for all AARs** — all 14 library modules ship R8-optimized bytecode (`optimization { minify = true }` + `-optimizationpasses 10` + `-allowaccessmodification`).
- **BenchLab KMP** — competitor benchmark (Dynamic vs SDPS vs Lib #2) that runs on Android, JVM, iOS, macOS and the browser.
- **BOM** — `appdimens-kmp-bom` version alignment for multi-module consumers.

[1.0.1]: https://github.com/bodenberg/appdimens-kmp/releases/tag/1.0.1
[1.0.0]: https://github.com/bodenberg/appdimens-kmp/releases/tag/1.0.0
