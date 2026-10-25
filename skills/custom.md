# Custom strategies: agent reference

Use when the user wants their own scaling (a formula, per-device rules, a shared JSON) instead of a built-in. Docs: `DOCUMENTATION/custom/` (start at `README.md`). Module `io.github.bodenberg:appdimens-kmp-custom`, pure common code (every KMP target).

## Decide the form

1. **A built-in passes visual QA?** Keep it. Custom strategies are for when none fits.
2. **The user describes math** ("scale by width over 360, between 0.85 and 1.8"):
   - multiplies every value → `CustomScaleStrategy { (width / 360f).coerceIn(0.85f, 1.8f) }`
   - computes the final value from `base` → `scalingFormula { base * pow(sw / 360f, 0.8f) }`
3. **The formula must live in JSON / remote config / design tokens** → text: `scalingFormula("base * pow(sw / 360, 0.8)")`, `16.cdp("…")`, JSON `"formula"`.
4. **Different behavior per device / screen range / orientation, bounds, font-scale cap** → `scalingStrategy("id") { … on(DeviceClass.TABLET) { … } }` or the JSON document.
5. **Several apps or platforms share it** → the JSON document, loaded once in common code (`ScalingStrategies.loadJson(text)`, e.g. from `Res.readBytes("files/appdimens-strategies.json").decodeToString()`).

## Cheat sheet

```kotlin
import com.appdimens.kmp.custom.*           // strategies (commonMain)
import com.appdimens.kmp.compose.custom.*   // 16.cdp(s): Dp · 14.csp(s): TextUnit · cdpPx / cspPx
import com.appdimens.kmp.code.custom.*      // 16.cdp(appContext, s): px · cdpDp · csp · cspUnits

val Product = CustomScaleStrategy { (width / 360f).coerceIn(0.85f, 1.8f) }
val Hud = scalingFormula { base * pow(sw / 360f, 0.8f) }
val Brand = scalingStrategy("brand") {
    reference(width = 360, height = 640); curve = power(0.8f); clamp(0.85f, 1.6f); clampFontScale(1.3f)
    on(DeviceClass.TABLET, DeviceClass.DESKTOP) { curve = logarithmic(0.4f) }
}
ScalingStrategies.default = Brand          // 16.cdp / 14.csp without argument
ProvideScalingStrategy(Hud) { … }          // Compose subtree
```

Scope names (code and text): `base` (formula only) `sw width height shortSide longSide diagonal perimeter aspectRatio ratio density fontScale`, flags `phone tablet desktop tv watch car vr landscape portrait multiWindow`, math `pow sqrt ln log10 exp abs round min max clamp lerp smoothstep`. `ratio = sw / 300`, so `base * ratio` = `sdp`.

## Rules an agent must follow

- **Declare strategies once** (top-level `val` in `commonMain`, `object AppStrategies`, or JSON). Never build `CustomScaleStrategy { }` / `scalingFormula { }` inside a composable body: each call is a new strategy and a new cache id. Inline text (`16.cdp("…")`) is shared per text and is fine.
- **Lambdas are pure**: no state, settings or time inside. Different state → different strategy or a rule.
- **Prefer a factor** when the math is `base × screen-term`: `CustomScaleStrategy`, a curve, or text shaped `base * (…)` all cost one multiply. `base * sw / 360` misses the text fast path; write `base * (sw / 360)`.
- **Value bounds** (`clampValue`) only for real absolute limits (tap targets, max text); they must be ≥ 0.
- **Text vs code**: code lambdas cannot be exported to JSON; use text when the user mentions JSON, remote config, A/B tests or other platforms.
- **`code.custom` gets px** (`16.cdp(appContext, s)`) and takes an `AppDimensContext` (`localAppDimensContext()` in Compose, `defaultPlatformContext()` on desktop / iOS / macOS / web, your own implementation on Linux / Windows); Compose gets `Dp` / `TextUnit`. There is no Android `Context` overload, no Java facade and no `loadAsset`.
- **Device classes differ per target**: desktop JVM / macOS / Linux / Windows report `desktop`; iOS and web only `phone` / `tablet`. Prefer size rules (`onScreen(minWidth = …)`, `largeScreens { }`) when the layout depends on size.
- Watch for **shadowing**: a local `val tv` hides the scope flag `tv` inside the braces.

## Verify

- `Brand.explainCurrent()` (Compose) / `Brand.explain(appContext)` prints the axis, curve, factor, applied rules and formula.
- Unit tests in `commonTest`: `Brand.factor(ScreenSnapshot(widthDp = 411f, heightDp = 891f))`, no device needed.
- JSON files: `npx appdimens-skills kit custom check <file>` and `kit custom explain --formula "<text>" --devices medium-phone,medium-tablet`.
- Errors carry the JSON path or the formula position; see `DOCUMENTATION/custom/troubleshooting.md`.
