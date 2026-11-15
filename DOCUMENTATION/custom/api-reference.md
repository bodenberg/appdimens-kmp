# API reference

## Building strategies (`com.appdimens.kmp.custom`, `commonMain`)

| Function | Returns | Notes |
|---|---|---|
| `CustomScaleStrategy(id?, register?) { factor }` | `ScalingStrategy` | programmable factor, `ScreenScope` receiver ([code formulas](code-formulas.md)) |
| `scalingFormula(id?, register?) { value }` | `ScalingStrategy` | code formula, `FormulaScope` receiver |
| `scalingFormula("text", id?, register?)` | `ScalingStrategy` | text formula, shared per text without an id ([text formulas](text-formulas.md)) |
| `scalingStrategy(id, register = true) { … }` | `ScalingStrategy` | full configuration ([configuration](configuration.md)) |
| `ScalingStrategy.extend(id, register = true) { … }` | `ScalingStrategy` | child of an existing strategy |
| `ScalingStrategy.withAxis(axis)` | `ScalingStrategy` | same strategy on another axis, id `"<id>@<axis>"`, cached per strategy |
| `ScalingConfig.toStrategy(id, register = true)` | `ScalingStrategy` | wrap a config built by hand |
| `BuiltInStrategies.SCALED` … `FIXED` · `CustomPresets.TV_SAFE` … | `ScalingStrategy` | ready-made parents |

## Registry: `ScalingStrategies`

| Member | Notes |
|---|---|
| `default` | strategy used by `16.cdp` / `14.csp` without an argument; initially `BuiltInStrategies.SCALED` |
| `register(strategy)` · `unregister(id)` | add or replace · remove |
| `get(id)` / `ScalingStrategies["id"]` · `getOrThrow(id)` | lookup, including built-ins and presets |
| `ids` | ids registered by the app |
| `formula("text")` | shared text-formula strategy |
| `loadJson(json): Map<String, ScalingStrategy>` | register a [JSON document](json.md) and apply its `default`; atomic |

The registry is thread-safe on every target (it uses `kotlinx.atomicfu` locks).

## Compose Multiplatform (`com.appdimens.kmp.compose.custom`, `composeMain`)

| Call | Returns |
|---|---|
| `16.cdp(strategy)` · `16.cdp` · `16.cdp("text")` | `Dp` |
| `16.cdpPx(strategy)` · `16.cdpPx` · `16.cdpPx("text")` | `Float` px |
| `14.csp(strategy)` · `14.csp` · `14.csp("text")` | `TextUnit` (font-scale policy applied) |
| `14.cspPx(strategy)` · `14.cspPx` · `14.cspPx("text")` | `Float` px |
| `strategy.dp(16)` · `strategy.sp(14)` · `strategy.px(16)` | strategy-first forms |
| `16.dp.scaledBy(strategy)` | scale an existing `Dp` |
| `ProvideScalingStrategy(strategy) { … }` · `LocalScalingStrategy` · `currentScalingStrategy` | scoped default |
| `strategy.currentFactor()` · `strategy.explainCurrent()` | debug |

Without an explicit strategy, `16.cdp` uses the nearest `ProvideScalingStrategy`, then `ScalingStrategies.default`. The window comes from `localAppDimensContext()` (the value of `AppDimensProvider`, or the platform default), like every `compose.*` package. Available on the Compose targets: Android, JVM desktop, iOS, macOS and js/wasmJs.

## No Compose (`com.appdimens.kmp.code.custom`, `commonMain`)

Short names return **px**, like every `code.*` package. Every call takes the platform-neutral window handle `AppDimensContext` (`appContext`); there is no Android `Context` overload and no Java facade.

| Call | Returns |
|---|---|
| `16.cdp(appContext, strategy = ScalingStrategies.default)` · `16.cdp(appContext, "text")` | `Float` px |
| `16.cdpDp(appContext, …)` | `Float` dp |
| `14.csp(appContext, …)` | `Float` px for text (font-scale policy applied) |
| `14.cspUnits(appContext, …)` | `Float` sp units (the platform applies its font scale; the policy is pre-compensated) |
| `strategy.px(appContext, 16)` · `.dp(appContext, 16)` · `.spPx(appContext, 14)` | strategy-first forms |
| `strategy.factor(appContext)` · `strategy.explain(appContext)` | debug |
| `appContext.screenSnapshot()` | the `ScreenSnapshot` strategies see |

Where `appContext` comes from:

| Target | `AppDimensContext` |
|---|---|
| Inside a composable (any Compose target) | `localAppDimensContext()` |
| JVM desktop · iOS · macOS · js/wasmJs, outside Compose | `defaultPlatformContext()` (screen-derived singleton) |
| Android, outside Compose | `defaultPlatformContext()` is `null`: keep the `localAppDimensContext()` of your composition, or implement `AppDimensContext` over the `Configuration` |
| Linux · Windows (no Compose) | your own `AppDimensContext` over a `ScreenConfiguration`; `defaultPlatformContext()` is `null` |

## Strategy members

| Member | Notes |
|---|---|
| `id` · `config` · `isValueDependent` | identity · full config · whether results depend on the value |
| `resolve(screen)` → `ResolvedScaling` | memoized per window: `factor`, `appliedRules`, `scale(base)`, `scaleSpDp(base)`, `scaleSpUnits(base)`, `explain(id)` |
| `factor(screen)` · `scale(base, screen)` · `explain(screen)` | platform-free helpers (tests, previews, `commonTest`) |
| `toJson()` | export (throws for code lambdas) |

`ScreenSnapshot(widthDp, heightDp, smallestWidthDp, density, fontScale, isLandscape, isMultiWindow, device)` builds a window by hand, for tests and previews. Every argument after `heightDp` has a default (`smallestWidthDp = min(w, h)`, `device` = tablet when `sw ≥ 600`, else phone). `DimenMetrics.toScreenSnapshot()` and `ScalingStrategy.resolve(metrics)` bridge the core `DimenMetrics`.

[Back to the custom docs](README.md)
