# Changelog

All notable changes to **AppDimens Dynamic KMP** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.2] — 2026-08-28

### Fixed (Compose-version crash reported after 1.0.1)

- **Consumer on a different Compose Multiplatform version crashes the app at startup ("module not found" / `NoClassDefFoundError`).** In 1.0.0 and 1.0.1 the convention plugin declared the Compose Multiplatform artifacts as `implementation` with the version hardcoded as a string literal in the precompiled script plugin (`composeVersion = "1.11.1"`). The library therefore shipped its own copy of the Compose runtime on the consumer's classpath. When a consumer's app was on a different Compose version (1.9.x, 1.10.x, a different 1.11.x patch, or a newer 1.12.x), Gradle resolved the highest version across the graph and the library's bytecode — compiled against 1.11.1 — referenced internal classes / methods that did not exist in the consumer's version → `NoClassDefFoundError`, `NoSuchMethodError`, "module not found" at app launch. The fix is two-layered:
  1. All Compose artifacts in the convention plugin (`appdimens.kmp-library`) are now **`compileOnly`** instead of `implementation`. The library never ships a second copy of Compose into the consumer's runtime classpath; the consumer is the single source of truth for the Compose stack at runtime. The version is now also read from the shared `gradle/libs.versions.toml` (no more duplicated string literal that can drift from the catalog).
  2. The convention plugin now publishes **Gradle `constraints { ... require(...) }`** blocks for `org.jetbrains.compose.runtime:runtime`, `ui`, `foundation` and `material3`. When a consumer does not pin a Compose version of their own, Gradle resolves exactly the version the library was compiled against, so the runtime ABI matches the bytecode. A consumer that deliberately wants a different Compose version can still override (Gradle conflict resolution lets the higher version win; the README documents the ABI-drift contract for that case).
  3. The `library-bom` (BOM artifact `io.github.bodenberg:appdimens-kmp-bom`) now also includes the four `org.jetbrains.compose.*` artifacts in its `api(...)` constraint set. Consumers who import the BOM get a one-line fix: `implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.2"))` aligns every Compose Multiplatform dependency in the project to the version AppDimens was compiled against, regardless of what their own Compose would otherwise resolve to.
  4. Internal test source set `composeTest` keeps Compose as `implementation` so module-level Compose tests still run on the producer's classpath; the test-time alignment uses the same catalog version, so tests always exercise the same Compose API the bytecode targets.
- **Catalog drift between the precompiled script plugin and `libs.versions.toml` was possible in 1.0.0/1.0.1.** The hardcoded `composeVersion = "1.11.1"` / `material3Version = "1.9.0"` / `coroutinesVersion = "1.10.2"` etc. in `appdimens.kmp-library.gradle.kts` could silently fall behind the catalog after a maintainer update. The convention plugin now reads all of these via the `libs.versions.*` accessors generated from the shared catalog (`build-logic/settings.gradle.kts` declares the same catalog via `versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }`), so the catalog is the single source of truth and the build fails fast if the catalog is malformed.

### Verified

- `./gradlew :library:compileKotlinJvm :library:compileDebugKotlinAndroid` builds the core module against the catalog-pinned Compose 1.11.1 with no duplicated `compose.runtime:runtime` on the publication classpath.
- `./gradlew :library-bom:dependencies --configuration api` shows the four `org.jetbrains.compose.*` constraints emitted by the BOM, so consumers that apply the BOM inherit the alignment.
- `./gradlew :library:dependencies --configuration compileClasspath` (with the convention plugin applied) lists Compose as `compileOnly` (absent from the published classpath) and exposes the four `require` constraints on the consumer side.

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
- **Broken wasmJs web distribution (samples).** The custom `index.html` in `app` and `benchlab` still referenced a legacy `skiko.js` script that Compose Multiplatform 1.11 no longer emits — the skiko runtime is bundled **inside** the webpack UMD bundle (`app.js` / `benchlab.js`), which fetches its `.wasm` chunks at runtime. The stale `<script src="skiko.js">` tag (404 in production) was removed from both templates; verified in headless Firefox that the fixed dist loads `index.html → bundle.js → both .wasm chunks` with zero 404s.

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
- **Published to Maven Central** — all 15 artifacts (`appdimens-kmp` core + 13 strategy modules + `appdimens-kmp-bom`) at `io.github.bodenberg:*:1.0.1`, GPG-signed, with full KMP variant matrix (Android AAR, JVM, iOS, macOS, JS, wasmJs, Linux x64/arm64, Windows mingwX64); consumer fixture resolved from Central (BOM + core + percent + power) and executed successfully

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
