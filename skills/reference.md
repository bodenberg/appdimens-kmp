# AppDimens KMP — library map (concise)

**Modules (1.0.1):** [DOCUMENTATION/MODULES.md](../DOCUMENTATION/MODULES.md)

**Doc base (repo root, Git ref `1.0.1`):** this repository (`appdimens-kmp`)

This file supplements [SKILL.md](SKILL.md). Read it when you need **package locations**, **symmetry between Compose and `code`**, or **core types**.

## Module layout

Full Gradle/Maven matrix: [MODULES.md](../DOCUMENTATION/MODULES.md) · [library-map.md](library-map.md).

- **`com.appdimens.kmp.common`** — `DpQualifier`, `Inverter`, `Orientation`, `UiModeType`, `UnitType`, `DpQualifierEntry`.
- **`com.appdimens.kmp.core`** — `DimenMetrics` (immutable per-window snapshot, eager AR computation), `DimenCache` (snapshot-partitioned; event-driven config watcher; specialized kernels: `resolveSdpPx`/`resolveSdpaPx`/`resolveHdpPx`/`resolveWdpPx` + DP variants; `fastMetricsForCode` for non-Compose fast lane; no persistence since 1.0.0), `StrategyFactorRegistry` / `SharedScreenMetrics` (compat only), `MissingModule` (Maven hint map → `appdimens-kmp` artifacts), `DimenCalculationPlumbing`, `DesignScaleConstants`, `AspectRatioLookup` (exact `ln`), `PercentSpaceMath` / `ResizeMath` / `ResizeBound`, platform-neutral `AppDimensContext` + `ScreenConfiguration`, Compose (`AppDimensProvider`, `LocalDimenMetrics`, `LocalAppDimensContext`, stamps, `rememberDimen*`).
- **`com.appdimens.kmp.compose.<strategy>`** / **`code.<strategy>`** — sources under `library/` (scaled + plain) or `library-<strategy>/`. Scaled's Compose package is top-level `compose` (files under `compose/scaled/`); its `code` package is top-level `code` (files under `code/scaled/`).
- **`compose.resize` / `code.resize`** — constraint resize (not `calculateRawScaling`).

Platform window-handle adapters (per `AppDimensContext`): `androidMain` wraps `android.content.Context` (auto-cached per raw Context); `jvmMain` wraps the AWT window; `iosMain` wraps `UIScreen`; `macosMain` wraps `NSScreen`; `jsMain`/`wasmJsMain` wrap the browser viewport; `linuxMain`/`mingwMain` have no windowing API (`defaultPlatformContext() = null` — build an `AppDimensContext` from a `ScreenConfiguration` or use the `DimenMetrics` overloads). Shared native code lives in `nativeMain`.

Build hint for missing satellites: `MissingModule` (`com.appdimens.kmp.core`) + the `checkAppDimensModules` Gradle check.

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

**Product / formal math:** [PRD.md](../DOCUMENTATION/PRD.md) · [PDR.md](../DOCUMENTATION/PDR.md) · [MATHEMATICS-AND-CALCULUS.md](../DOCUMENTATION/MATHEMATICS-AND-CALCULUS.md)

## Example application module (`app`)

Upstream sample modules only (not shipped inside `io.github.bodenberg:appdimens-kmp`); use for pattern reference:

- **KMP demo (`app`)** — [`app/README.md`](../app/README.md). Compose Multiplatform sample: `app/src/commonMain/kotlin/com/example/app/compose/SdpDemoScreen.kt` (interactive SDP/HDP/WDP demo + auto-resize) and `DemoCalcRouting.kt`. Entry points per platform: `jvmMain/main.kt` (desktop), `iosMain/MainViewController.kt`, `macosMain/main.kt`, `jsMain`/`wasmJsMain/main.kt` (web).
- **Android-only demo (`app-android`)** — release builds with R8 + `proguard-rules.pro`.
- **BenchLab (`benchlab` / `benchlab-android`)** — competitor benchmark (Dynamic vs SDPS vs Lib #2) that runs on Android, JVM, iOS, macOS and the browser; results in [PERFORMANCE.md](../PERFORMANCE.md) / [PERFORMANCE-COMPARATIVE.md](../PERFORMANCE-COMPARATIVE.md).

## Internal `DimenCache.CalcType` (debug / cache tagging)

Maps to package families: AUTO, DIAGONAL, FILL, FIT, FLUID, INTERPOLATED, LOGARITHMIC, PERCENT, PERIMETER, POWER, RESIZE, SCALED, UNITIES, ASPECT_RATIO, DENSITY — see `library/src/commonMain/kotlin/com/appdimens/kmp/core/DimenCache.kt`. End users think in **strategy names** and imports, not this enum.

## What this reference intentionally skips

Per the skill: **do not** expand on **`ignoreMultiWindows`** or **`*i` / `*ia`** suffix workflows when guiding users — those are omitted from the interactive workflow.
