# AppDimens Dynamic KMP — Gradle / Maven modules (1.0.1)

## Module graph

```
app (KMP demo: Android + JVM + iOS + macOS + js + wasmJs)   app-android (Android-only demo)
benchlab (KMP benchmark)                                     benchlab-android (Android-only benchmark)
 │
 ├── library                      → io.github.bodenberg:appdimens-kmp
 ├── library-bom                  → …:appdimens-kmp-bom
 ├── library-auto                 → …:appdimens-kmp-auto
 ├── library-density              → …:appdimens-kmp-density
 ├── library-diagonal             → …:appdimens-kmp-diagonal
 ├── library-fill                 → …:appdimens-kmp-fill
 ├── library-fit                  → …:appdimens-kmp-fit
 ├── library-fluid                → …:appdimens-kmp-fluid
 ├── library-interpolated         → …:appdimens-kmp-interpolated
 ├── library-logarithmic          → …:appdimens-kmp-logarithmic
 ├── library-percent              → …:appdimens-kmp-percent
 ├── library-perimeter            → …:appdimens-kmp-perimeter
 ├── library-power                → …:appdimens-kmp-power
 ├── library-resize               → …:appdimens-kmp-resize
 └── library-units                → …:appdimens-kmp-units
```

Satellites depend only on `:library` (`api(project(":library"))`). Android Gradle `namespace` values are unique per module (`com.appdimens.kmp` for the principal, `com.appdimens.kmp.<strategy>` for satellites). Kotlin packages remain `com.appdimens.kmp.*`.

## Targets (KMP)

Every library module (`:library` and all satellites) compiles for:

| Target | Gradle declaration |
|--------|--------------------|
| **Android** (min SDK 24) | `android { namespace …; compileSdk 37; minSdk 24; optimization { minify = true } }` |
| **JVM** (Java 17) | `jvm()` |
| **iOS device** | `iosArm64()` |
| **iOS simulator (Apple Silicon)** | `iosSimulatorArm64()` |
| **macOS** | `macosArm64()` |
| **Web / Kotlin/JS (IR)** | `js(IR)` |
| **Web / wasmJs (browser)** | `wasmJs { browser() }` |
| **Linux native** (`code` API only) | `linuxX64()` + `linuxArm64()` |
| **Windows native** (`code` API only) | `mingwX64()` |

`commonMain` holds all strategy math, cache and Compose APIs; `androidMain` / `jvmMain` / `iosMain` / `macosMain` / `jsMain` / `wasmJsMain` / `linuxMain` / `mingwMain` hold only the platform window-handle adapters (`expect`/`actual`). `nativeMain` shares native code between iOS and macOS. On Linux/Windows the shared `appdimens.kmp-library` convention plugin sets `defaultPlatformContext() = null` (no windowing API in the Kotlin/Native stdlib) — build an `AppDimensContext` from a `ScreenConfiguration` or use the `DimenMetrics` overloads directly.

## Artifacts

| Artifact | Contents |
|----------|----------|
| `appdimens-kmp` | `common`, `core`, **scaled**, **plain** |
| `appdimens-kmp-<strategy>` | `code.<strategy>` + `compose.<strategy>` |
| `appdimens-kmp-bom` | Version constraints for the set above (`java-platform`) |

All published coordinates share `appdimens.version` in `gradle.properties` (**1.0.1**).

## Installation

```kotlin
// commonMain.dependencies (or the platform source set you target)
implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.1"))
implementation("io.github.bodenberg:appdimens-kmp")
implementation("io.github.bodenberg:appdimens-kmp-percent")
```

Without the BOM, pin the same version on each coordinate. See [README — Installation](../README.md#installation-v101).

## Core layout

- The source of truth is the immutable **`DimenMetrics`** window snapshot (size, density, font scale, orientation, ui mode, multi-window). Shared screen metrics (`scale`, aspect ratio, density) are derived from it once per snapshot.
- **Event-driven config watcher**: on Android a `ComponentCallbacks2` listener invalidates fast slots synchronously on any real configuration change; on desktop/web/iOS/macOS the providers rebuild the snapshot from the live window configuration.
- **Specialized kernels**: `resolveSdpPx`, `resolveSdpDp`, `resolveSdpaPx`, `resolveSdpaDp`, `resolveHdpPx`, `resolveHdpDp`, `resolveWdpPx`, `resolveWdpDp` — one kernel per family/qualifier, zero branches, volatile load + identity compare + legacy multiply order.
- **`fastMetricsForCode`**: Non-Compose fast-lane resolution — skips the ThreadLocal probe entirely.
- **Compose fast lane**: reads `metricsScope ?: LocalDimenMetrics` (one CompositionLocal + one multiply) — works on every platform without `LocalContext`.
- **DimenMetrics eager AR**: `normalizedAspectRatio` and `logNormalizedAspectRatio` are plain `val` — no hidden `synchronized` probe in the SDPA fast lane.
- Strategy-specific scales (`diagonal`, `power`, `logarithmic`, `interpolated`, `perimeter`) are derived lazily from `DimenCache.currentMetrics` at resolution time — no process-global pre-computation. `StrategyFactorRegistry` remains as a source-compatibility hook.
- `CalcType` ordinals live in core so cache keys stay stable across modules.
- **R8 pre-shrink**: every Android AAR is R8-optimized at build time (`optimization { minify = true }` + `-optimizationpasses 10` + `-allowaccessmodification`, `-dontobfuscate`) — consumers get optimized bytecode even in debug. See [R8-PROGUARD.md](../R8-PROGUARD.md).

## See also

- [DOCUMENTATION/README.md](README.md) — strategy guides
- [R8-PROGUARD.md](../R8-PROGUARD.md) — per-AAR consumer rules
- [CONTRIBUTING.md](../CONTRIBUTING.md) — module contribution rules
