# AppDimens KMP — library map

**Doc base (repo root, Git ref `1.0.1`):** this repository (`appdimens-kmp`)

Read this file when you need package locations, Compose↔`code` symmetry, or core types.

> **Identity:** this is the **Kotlin Multiplatform** library — Maven coordinates `io.github.bodenberg:appdimens-kmp*`, packages `com.appdimens.kmp.*`. The Android-only predecessor (`appdimens-dynamic`, `com.appdimens.dynamic.*`) is a different artifact; never mix them.

---

## Gradle / Maven modules (1.0.1)

| Strategy | Gradle project | Maven artifact | Source roots (commonMain) |
|---|---|---|---|
| scaled (+ common/core/plain) | `:library` | `appdimens-kmp` | `library/src/commonMain/kotlin/com/appdimens/kmp/{common,core,code/plain,code/scaled,compose/scaled}` |
| *(BOM)* | `:library-bom` | `appdimens-kmp-bom` | `library-bom/build.gradle.kts` (`java-platform`) |
| auto | `:library-auto` | `appdimens-kmp-auto` | `library-auto/.../com/appdimens/kmp/{code,compose}/auto` |
| density | `:library-density` | `appdimens-kmp-density` | `library-density/.../density` |
| diagonal | `:library-diagonal` | `appdimens-kmp-diagonal` | `library-diagonal/.../diagonal` |
| fill | `:library-fill` | `appdimens-kmp-fill` | `library-fill/.../fill` |
| fit | `:library-fit` | `appdimens-kmp-fit` | `library-fit/.../fit` |
| fluid | `:library-fluid` | `appdimens-kmp-fluid` | `library-fluid/.../fluid` |
| interpolated | `:library-interpolated` | `appdimens-kmp-interpolated` | `library-interpolated/.../interpolated` |
| logarithmic | `:library-logarithmic` | `appdimens-kmp-logarithmic` | `library-logarithmic/.../logarithmic` |
| percent | `:library-percent` | `appdimens-kmp-percent` | `library-percent/.../percent` |
| perimeter | `:library-perimeter` | `appdimens-kmp-perimeter` | `library-perimeter/.../perimeter` |
| power | `:library-power` | `appdimens-kmp-power` | `library-power/.../power` |
| resize | `:library-resize` | `appdimens-kmp-resize` | `library-resize/.../resize` |
| units | `:library-units` | `appdimens-kmp-units` | `library-units/.../units` |

Full graph: [DOCUMENTATION/MODULES.md](../DOCUMENTATION/MODULES.md). Satellites depend only on `:library`. `:library-bom` publishes version constraints.

## Targets (every library module)

| Target | Source set | Notes |
|--------|-----------|-------|
| Android (min SDK 24) | `androidMain` | `optimization { minify = true }` (R8 pre-shrink); `AppDimensContext` wraps `Context` |
| JVM (Java 17) | `jvmMain` | AWT window handle |
| iOS device | `iosMain` | `UIScreen` handle |
| iOS simulator (Apple Silicon) | `iosMain` | `iosSimulatorArm64` |
| macOS | `macosMain` | `NSScreen` handle |
| Web / Kotlin/JS (IR) | `jsMain` | browser viewport |
| Web / wasmJs | `wasmJsMain` | browser viewport |
| Linux native | `linuxMain` | `code` API only — `defaultPlatformContext() = null` |
| Windows native | `mingwMain` | `code` API only — `defaultPlatformContext() = null` |
| Shared native | `nativeMain` | code shared between iOS and macOS |

## Package layout (packages span `:library` and `:library-*`)

- **`com.appdimens.kmp.common`** — shared enums/value types: `DpQualifier` (SMALL_WIDTH, HEIGHT, WIDTH), `Inverter`, `Orientation`, `UiModeType`, `UnitType`, `DpQualifierEntry`.
- **`com.appdimens.kmp.core`** — cross-cutting engine: `DimenMetrics` (immutable per-window snapshot: size, density, font scale, orientation, ui mode, multi-window; eager AR computation), `DimenCache` (snapshot-partitioned cache, event-driven config watcher, specialized kernels: `resolveSdpPx`/`resolveSdpaPx`/`resolveHdpPx`/`resolveWdpPx` + DP variants, `fastMetricsForCode` for the non-Compose fast lane; explicit invalidation not required for correctness — `invalidateOnConfigChange` is a compat hook; no disk persistence; stable `CalcType` ordinals), `StrategyFactorRegistry` / `SharedScreenMetrics` (source-compatibility hook), `MissingModule` (Maven hint map → `appdimens-kmp*`), `DimenCalculationPlumbing` (qualifier resolution, screen dp reads, aspect-ratio multiplier), `DesignScaleConstants`, `AspectRatioLookup` (exact `ln`), percent/resize math (`PercentSpaceMath`, `ResizeMath`, `ResizeBound`, `AutoResizePercentBasis`), platform-neutral window handle (`AppDimensContext`, `ScreenConfiguration`, `defaultPlatformContext` expect/actual), Compose integration (`CompositionLocals` / `AppDimensProvider`, `LocalUiModeType`, `LocalDimenMetrics`, `LocalAppDimensContext`, `ComposeRememberStamps`, `ComposeDimenRemember`).
- **`com.appdimens.kmp.compose.<strategy>`** — one folder per scaling strategy for Compose UI (e.g. `compose/percent/DimenPercent.kt`, `DimenPercentDpExtensions.kt`, …). Scaled lives at `compose/scaled/` but the package is top-level `com.appdimens.kmp.compose`.
- **`com.appdimens.kmp.code.<strategy>`** — mirror for non-Compose code: `DimenSdp`, `DimenSsp`, `*DpExtensions`, `*SpExtensions`, `DimenScaled`, `Dimen*PlainPx.kt`, `code/plain/DimenPlainBranch.kt`. All `code` entry points take an **`AppDimensContext`** window handle (not an Android `Context`).
- **`com.appdimens.kmp.compose.resize`** / **`com.appdimens.kmp.code.resize`** — constraint-based resize (binary search over discrete px steps, "fits" predicate); distinct from `calculateRawScaling` curves.

---

## Strategy → documentation file

| Folder suffix | Doc |
|---------------|-----|
| scaled | [DOCUMENTATION/scaled.md](../DOCUMENTATION/scaled.md) |
| percent | [DOCUMENTATION/percent.md](../DOCUMENTATION/percent.md) |
| power | [DOCUMENTATION/power.md](../DOCUMENTATION/power.md) |
| fluid | [DOCUMENTATION/fluid.md](../DOCUMENTATION/fluid.md) |
| auto | [DOCUMENTATION/auto.md](../DOCUMENTATION/auto.md) |
| diagonal | [DOCUMENTATION/diagonal.md](../DOCUMENTATION/diagonal.md) |
| fill | [DOCUMENTATION/fill.md](../DOCUMENTATION/fill.md) |
| fit | [DOCUMENTATION/fit.md](../DOCUMENTATION/fit.md) |
| interpolated | [DOCUMENTATION/interpolated.md](../DOCUMENTATION/interpolated.md) |
| logarithmic | [DOCUMENTATION/logarithmic.md](../DOCUMENTATION/logarithmic.md) |
| perimeter | [DOCUMENTATION/perimeter.md](../DOCUMENTATION/perimeter.md) |
| density | [DOCUMENTATION/density.md](../DOCUMENTATION/density.md) |
| resize | [DOCUMENTATION/resize.md](../DOCUMENTATION/resize.md) |
| units (physical) | [DOCUMENTATION/physical-units.md](../DOCUMENTATION/physical-units.md) |

**Formal docs:** [PRD.md](../DOCUMENTATION/PRD.md) · [PDR.md](../DOCUMENTATION/PDR.md) · [MATHEMATICS-AND-CALCULUS.md](../DOCUMENTATION/MATHEMATICS-AND-CALCULUS.md)  
**API detail:** [DOCUMENTATION/index.md](../DOCUMENTATION/index.md) · per-strategy guides listed above

---

## Example application modules

Upstream sample modules only — not in the Maven artifacts. Use for pattern reference.

- **KMP demo (`app`)** — [app/README.md](../app/README.md)
  - Compose Multiplatform: `app/src/commonMain/kotlin/com/example/app/compose/SdpDemoScreen.kt` (interactive SDP/HDP/WDP demo, auto-resize examples) · `DemoCalcRouting.kt`
  - Entry points: `jvmMain/kotlin/com/example/app/main.kt` (desktop) · `iosMain/kotlin/com/example/app/MainViewController.kt` · `macosMain/kotlin/com/example/app/main.kt` · `jsMain`/`wasmJsMain/kotlin/com/example/app/main.kt` (web)
- **Android-only demo (`app-android`)** — release builds with R8 (`proguard-rules.pro`, `-optimizationpasses 10`).
- **BenchLab (`benchlab` / `benchlab-android`)** — competitor benchmark (Dynamic vs SDPS vs Lib #2) on Android, JVM, iOS, macOS and web; `AUTO_START` flag for headless runs; results in [PERFORMANCE.md](../PERFORMANCE.md) / [PERFORMANCE-COMPARATIVE.md](../PERFORMANCE-COMPARATIVE.md).

---

## Internal `DimenCache.CalcType`

Debug/cache tagging only — end users think in strategy names. Values: AUTO, DIAGONAL, FILL, FIT, FLUID, INTERPOLATED, LOGARITHMIC, PERCENT, PERIMETER, POWER, RESIZE, SCALED, UNITIES, ASPECT_RATIO, DENSITY.  
Source: `library/src/commonMain/kotlin/com/appdimens/kmp/core/DimenCache.kt`

---

## What this file intentionally omits

`ignoreMultiWindows`, `*i`, and `*ia` suffix workflows — omitted from the interactive guidance.
