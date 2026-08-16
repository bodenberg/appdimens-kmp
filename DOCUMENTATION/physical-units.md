# Physical units (`compose` + `code.units`)

**Artifact:** `io.github.bodenberg:appdimens-kmp-units:1.0.1` (`:library-units`)
See [MODULES.md](MODULES.md) · [README installation](../README.md)

## What it is

Conversion of **real-world** measures (millimeters, centimeters, inches) using the platform display metrics behind the window handle. It does **not** follow the “300 dp axis” scaling model; use when you need **approximate physical size** on the device — on any platform (Android, JVM, iOS, macOS, Web).

## Calculation used

- **`toMm(mm, appContext?)`**: mm × xdpi ÷ 25.4 ÷ **density** → **dp** as `Float`; cached in **`DimenCache`** (`CalcType.UNITIES`).
- **`toCm(cm, appContext?)`**: `cm × 10` through the same mm path → **dp** `Float` (cached like `toMm`).
- **`toInch(inches, appContext?)`**: inches × xdpi ÷ 25.4 ÷ **density** → **dp** `Float` (cached like `toMm`).
- **Composable** `Float` / `Int` **`.mm`**, **`.cm`**, **`.inch`**: resolve the window handle via `localAppDimensContext()` and return a **`Float` in dp-equivalent units** (not layout pixels). For Compose layout, use **`.dp`** on the result, e.g. `10f.mm.dp`.

Pure helpers (`convertMmToCm`, `inchToMm`, …) are **math-only** between unit labels and need no context.

**Code module (`com.appdimens.kmp.code.units.DimenPhysicalUnits`):** the same conversions for non-Compose code (pass an `AppDimensContext` obtained from `localAppDimensContext()` inside a Composable, or a platform adapter on Android).

## How to use

**Compose** (import `com.appdimens.kmp.compose.mm`, `.cm`, `.inch`):

```kotlin
import androidx.compose.ui.unit.dp
import com.appdimens.kmp.compose.mm
import com.appdimens.kmp.compose.cm

Modifier.width(10f.mm.dp)
Modifier.height(2.5f.cm.dp)
```

| Composable property | Receivers | Return | Typical use |
|--------------------|-----------|--------|-------------|
| `mm` | `Float`, `Int` | `Float` (dp) | `10f.mm.dp` → `Dp` for `Modifier` |
| `cm` | `Float`, `Int` | `Float` (dp) | `2.5f.cm.dp` |
| `inch` | `Float`, `Int` | `Float` (dp) | `1f.inch.dp` |

**Object `DimenPhysicalUnits` (Compose) — non-composable helpers** (all accept an optional `AppDimensContext?`):

| API | Role | Example |
|-----|------|---------|
| `toMm(mm, appContext?)` | mm → dp `Float` | `DimenPhysicalUnits.toMm(10f)` |
| `toCm(cm, appContext?)` | cm → dp `Float` | `DimenPhysicalUnits.toCm(2.5f)` |
| `toInch(inches, appContext?)` | inch → dp `Float` | `DimenPhysicalUnits.toInch(1f)` |
| `convertMmToCm` / `convertMmToInch` | pure `Float` | `convertMmToCm(100f)` |
| `convertCmToMm` / `convertCmToInch` | pure `Float` | `convertCmToInch(2.54f)` |
| `convertInchToCm` / `convertInchToMm` | pure `Float` | `convertInchToMm(1f)` |
| `Float.mmToCm()`, `Number.mmToCm()`, … | sugar over `convert*` | `5f.mmToInch()` |
| `radius(diameter, type, appContext?)` | half-size in **dp** | `radius(24f, UnitType.MM)` |
| `displayMeasureDiameter(diameter, isCircumference)` | scale for circumference | `displayMeasureDiameter(48f, true)` |
| `Float.radius(type)` / `Number.radius(type)` | Composable radius in **dp** | `48f.radius(UnitType.MM)` |
| `Float.measureDiameter(isCircumference)` | Composable toggle | `48f.measureDiameter(true)` |
| `unitSizeInDp(type, appContext?)` | size of **1.0** logical unit in **dp** (mm/cm/inch/dp/sp/px normalized to dp) | `unitSizeInDp(UnitType.MM)` |

Use **`UnitType`** (`MM`, `CM`, `INCH`, `SP`, `DP`, `PX`, …) with `radius` and `unitSizeInDp`.

## Why use it

Specs from **print**, **regulation** (touch target in mm), ruler-based prototyping, or **physical** mockups.

## When to use it

- Minimum tap target in **mm**.
- Matching a **cm** spec.
- When stakeholders reason in **inches** / **mm**, not dp.

## Advantages and trade-offs

- **Pros:** speaks “real world”; orthogonal to scaling strategies; works on every KMP platform.
- **Cons:** real vs nominal dpi varies (especially on desktop/web where the OS reports display dpi); not a substitute for **scaled** / breakpoints for layout grids.

## Recommended usage strategy

Use for **legal / a11y minimums** and isolated measurements; build the main **grid** with **scaled** (or another strategy).

[Back to index](README.md)
