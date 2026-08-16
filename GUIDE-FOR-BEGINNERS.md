# 📚 Complete Guide: AppDimens Dynamic KMP – All Calculations for Beginners

## Quick Introduction

You write **once** and your code automatically adapts to any device — **phone, tablet, desktop window, iOS, macOS, browser, Linux or Windows**. The library provides **14 different strategies**, each with its own “growth formula”, and the same Kotlin API runs on every platform (Linux/Windows native expose the `code` API).

---

## Installation (v1.0.1)

```kotlin
// commonMain.dependencies (or the platform source set you target)
implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.1"))
implementation("io.github.bodenberg:appdimens-kmp")
implementation("io.github.bodenberg:appdimens-kmp-percent")
implementation("io.github.bodenberg:appdimens-kmp-resize")
```

Without the BOM, pin `:1.0.1` on each coordinate. Details: [README](./README.md) · [MODULES.md](./DOCUMENTATION/MODULES.md).

| Strategy in this guide | Maven module |
|------------------------|--------------|
| **Scaled** (`sdp` / `hdp` / `wdp` / `ssp`) | `appdimens-kmp` |
| Percent, Power, Fluid, Auto, Diagonal, Fill, Fit, Interpolated, Logarithmic, Perimeter, Density | `appdimens-kmp-<name>` |
| Resize (`autoResize*`) | `appdimens-kmp-resize` |
| Physical units (mm / cm / in) | `appdimens-kmp-units` |

---

## 🎯 The 3 Core Pillars

### **1️⃣ SDP** (Smallest Dimension Proportion) – The Smallest Side

```
Uses: SHORTEST side of the window
Best for: Padding, spacing, general sizes
```

### **2️⃣ HDP** (Height Dimension Proportion) – Height

```
Uses: Window HEIGHT only
Best for: Component height, vertical lists
```

### **3️⃣ WDP** (Width Dimension Proportion) – Width

```
Uses: Window WIDTH only
Best for: Horizontal components
```

---

## 📊 The 14 Calculation Strategies

### **#1. SCALED (Default) ⭐**

**Reality check:** In practice, **Scaled is the strategy people use the most** and it covers **most everyday screens**. Stick to plain **`sdp` / `hdp` / `wdp` / `ssp`** when you want simple proportional growth. Add **`a`** (aspect ratio), e.g. **`16.sdpa`** or **`16.sspa`**, when tall tablets or unusual aspect ratios need a **smoother, refined** adjustment — still Scaled, just with the extra flag.

**Code (Compose Multiplatform — same code on Android, desktop, iOS, macOS, web):**

```kotlin
// Compose
Modifier.padding(16.sdp)   // SDP
Modifier.height(100.hdp)   // HDP
Modifier.width(200.wdp)    // WDP
Text("Text", fontSize = 16.ssp)

// Non-Compose (all platforms) — pass an AppDimensContext window handle
DimenSdp.sdp(appContext, 16)
```

**Formula (Simple):**

```
Result = Value × (Screen_Size / 300)
```

**What it means:**

* 300 dp is the reference “normal” size
* Larger screens → bigger values
* **Linear and predictable growth**

**Example:**

```
Phone 360 dp:   16 × (360/300) = 19.2
Tablet 480 dp:  16 × (480/300) = 25.6
Tablet 600 dp:  16 × (600/300) = 32
TV 1200 dp:     16 × (1200/300) = 64
```

**When to use:**
✅ Default layouts
✅ Spacing and padding
✅ General sizes
✅ **Start here!**

---

### **#2. AUTO (Automatic) ⭐⭐**

**Code:**

```kotlin
Modifier.padding(16.asdp)
Modifier.height(100.ahdp)
Modifier.width(200.awdp)
Text("Text", fontSize = 16.assp)
```

**Formula:**

```
Up to 480 dp:
  Same as SCALED

Above 480 dp:
  Result = (480/300) × Value + 0.4 × ln(1 + (Size - 480) / 300)
```

**What it means:**

* Phones → behaves like SCALED
* Tablets → **much slower growth**
* Prevents oversized UI on large screens

**When to use:**
✅ Phone + tablet with one token set
✅ When SCALED grows too much

---

### **#3. PERCENT**

#### **A) Screen share (`space*`) — the number *is* the %**

**Code:**

```kotlin
Modifier.width(10.spaceW)   // 10% width
Modifier.height(20.spaceSw) // 20% smallest side
```

**Formula:**

```
Result = (Percentage / 100) × Screen_Size
```

**What it means:**

* Always keeps the same **percentage** of the chosen axis
* Fully proportional layouts

**When to use:**
✅ Grids, columns
✅ Exact percentages of width, height, or smallest width

---

#### **B) Tokens like Scaled (`psdp` / `phdp` / `pwdp` / `pssp`)**

**Code:**

```kotlin
import com.appdimens.kmp.compose.percent.psdp
import com.appdimens.kmp.compose.percent.phdp
import com.appdimens.kmp.compose.percent.pwdp
import com.appdimens.kmp.compose.percent.pssp

Modifier.padding(12.psdp)
Modifier.height(40.phdp)
Modifier.width(30.pwdp)
Text("Hi", fontSize = 14.pssp)
```

| You want              | Typical extension | Idea                                      |
| --------------------- | ----------------- | ----------------------------------------- |
| SW-based (like `sdp`) | `16.psdp`         | Design token + **percent** strategy       |
| Height axis           | `16.phdp`         | Same pattern on height                    |
| Width axis            | `16.pwdp`         | Same pattern on width                     |
| Text (like `ssp`)     | `16.pssp`         | Sp on smallest-width axis, percent rules  |

**Formula:**

```
Without aspect ratio: same proportional idea as SCALED — roughly Value × (Axis / 300)
With `a`: percent strategy + aspect-ratio correction (library handles the curve)
```

**When to use:**
✅ You want **percent** scaling rules but **`sdp`-style** names across the codebase
✅ Mixing with **`space*`** when some sizes are “% of axis” and others are tokens

---

### **#4. POWER (Sublinear Growth)**

**Formula:**

```
Result = Value × (Size / 300)^0.75
```

**What it means:**

* Slower than linear scaling
* Prevents oversized elements

**When to use:**
✅ Icons
✅ Large displays (TVs, ultrawide)

---

### **#5. FLUID (Comfort Zone)**

**Formula:**

```
≤320 dp → 0.8×
≥768 dp → 1.2×
Between → linear interpolation
```

**What it means:**

* Keeps values within a stable range
* Avoids big jumps on phones

**When to use:**
✅ Typography on mobile
✅ Stable spacing

---

### **#6. DIAGONAL**

**Formula:**

```
Diagonal = √(width² + height²)
Result = Value × (Diagonal / 611.63)
```

**What it means:**

* Uses total window size
* More “real” scaling

**When to use:**
✅ Games
✅ Dashboards

---

### **#7. FILL (CSS “cover”)**

**Formula:**

```
Result = Value × max(width_ratio, height_ratio)
```

**What it means:**

* Uses the **largest axis**
* Fills the window

**When to use:**
✅ Backgrounds
✅ Hero sections

---

### **#8. FIT (CSS “contain”)**

**Formula:**

```
Result = Value × min(width_ratio, height_ratio)
```

**What it means:**

* Uses the **smallest axis**
* Prevents overflow

**When to use:**
✅ Forms
✅ Reading layouts

---

### **#9. INTERPOLATED**

**Formula:**

```
Result = Value + (Linear - Value) × 0.5
```

**What it means:**

* 50% fixed + 50% scalable
* Smooth growth

**When to use:**
✅ Fine-tuning UI scaling

---

### **#10. LOGARITHMIC**

**Formula:**

```
Result = Value × (1 + 0.4 × ln(Size / 300))
```

**What it means:**

* Very smooth growth
* Works across all window sizes

**When to use:**
✅ One token for all devices

---

### **#11. PERIMETER**

**Formula:**

```
Perimeter = width + height
Result = Value × (Perimeter / 833)
```

**What it means:**

* Uses both axes (sum)
* Balanced scaling

**When to use:**
✅ Cards
✅ Thumbnails

---

### **#12. DENSITY (DPI-based)**

**Formula:**

```
Result = Value × (DPI / 160)
```

**What it means:**

* Based on pixel density
* Not window size

**When to use:**
✅ Legacy bitmap assets

---

### **#13. RESIZE — sizes that fit inside a box**

**Idea in plain words:** Sometimes a number (font size, icon side, width) must be **as large as possible** but still **fit** inside the space the parent gives you — for example a long title in a card. **Resize** does **not** use the “screen ÷ 300” formula by itself; it **tries several sizes** until it finds the biggest one that still fits.

**When to use:**
✅ Headlines that must not overflow
✅ Icons or squares that should use “all the room” in a cell
✅ Anything where the **parent size** matters more than the global window size

**Rule:** Call these functions **only inside** `BoxWithConstraints { ... }`:

```kotlin
import androidx.compose.foundation.layout.BoxWithConstraints
import com.appdimens.kmp.compose.resize.autoResizeTextSp

BoxWithConstraints(Modifier.fillMaxWidth()) {
    val fontSize = autoResizeTextSp(
        text = "Very long product name that might not fit",
        minSp = 12,
        maxSp = 28,
        stepSp = 1,
        maxLines = 2,
    )
    Text(
        text = "Very long product name that might not fit",
        fontSize = fontSize,
        maxLines = 2,
    )
}
```

**Example — largest square that fits:**

```kotlin
import com.appdimens.kmp.compose.resize.autoResizeSquareSize

BoxWithConstraints(Modifier.fillMaxSize()) {
    val side = autoResizeSquareSize(min = 24, max = 120, step = 4)
    Box(Modifier.size(side)) { /* icon or avatar */ }
}
```

### **#14. PHYSICAL UNITS — real-world size (mm, cm, inch)**

**Idea:** Say “**10 mm** on screen” instead of guessing dp. The library converts using the device’s **density**.

**Typical use:** Rulers, print-like layouts, specs from design in **mm** or **inch**.

```kotlin
import androidx.compose.ui.unit.dp
import com.appdimens.kmp.compose.DimenPhysicalUnits

@Composable
fun RulerBar() {
    val widthDp = DimenPhysicalUnits.toMm(10f)   // 10 mm → dp
    val heightDp = DimenPhysicalUnits.toInch(0.25f)
    Modifier
        .width(widthDp.dp)
        .height(heightDp.dp)
}
```

There are helpers in **`com.appdimens.kmp.code.units`** for non-Compose code. Full table: [DOCUMENTATION/physical-units.md](DOCUMENTATION/physical-units.md).

---

## 🔧 Modifiers (Suffixes)

| Suffix   | Meaning                 |
| -------- | ----------------------- |
| *(none)* | Default                 |
| `a`      | Aspect ratio adjustment |
| `i`      | Ignore split-screen     |
| `ia`     | Both                    |

---

## 🔄 Orientation Inverters

| Inverter | Behavior            |
| -------- | ------------------- |
| `Ph`     | Height in portrait  |
| `Pw`     | Width in portrait   |
| `Lw`     | Width in landscape  |
| `Lh`     | Height in landscape |

---

## 📱 Quick Comparison

```
Need → Use

Default → SCALED
Phone + Tablet → AUTO
Exact % → PERCENT
Slow growth → POWER
Stable phone UI → FLUID
Full screen → FILL
No overflow → FIT
Balanced → INTERPOLATED
All devices → LOGARITHMIC
Cards → PERIMETER
Legacy → DENSITY
Fit inside a box → RESIZE
Real-world units → UNITS
```

---

## 🚀 Practical Example

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = 16.sdp,
            vertical = 12.asdp
        )
) {
    Column(
        modifier = Modifier.padding(16.sdp)
    ) {
        Text(
            "My Card",
            fontSize = 24.ssp
        )

        Text(
            "Card description",
            fontSize = 16.ssp
        )

        Button(
            modifier = Modifier
                .height(48.sdp)
                .fillMaxWidth()
        ) {
            Text("Click", fontSize = 16.ssp)
        }
    }
}
```

---

## 💡 Final Tips

1. Start with **SCALED**
2. If it grows too much → use **AUTO**
3. For universal tokens → use **LOGARITHMIC**
4. For backgrounds → use **FILL**
5. For reading → use **FIT**
6. For parent-constrained sizes → use **RESIZE**

---

## 🎓 Quick Recap

You only need 3 strategies to start:

1. **SCALED**
2. **AUTO**
3. **PERCENT**

Modifiers:

* `a` → aspect ratio
* `i` → ignore split-screen

**Start simple and scale when needed.** 🚀

---

## 📏 `…Px` extensions — when you need **pixels**, not `Dp`

**Idea:** `16.sdp` gives a **`Dp`** for layouts. Sometimes you need a **`Float` in px** (Canvas, custom drawing, old interop).

**Pattern:** Add **`Px`** to the name.

```kotlin
val strokeWidthPx = 2f.sdpPx      // Float in px, SDP-based
val paddingPx = 16.hdpPx          // height axis, px
```

Same **suffixes** (`a`, `i`, `ia`, inverters) exist on many **Px** properties — check the API for your strategy.

---

## 🔤 `sem` / `hem` / `wem` — scalable **Sp** that **ignores system font size**

**Problem:** `16.ssp` scales with the window **and** usually follows the user’s **font scale** (Accessibility). Sometimes you want window-based size but **not** to grow with “larger text” in settings.

**Fix:** “Fixed em” style extensions on the **scaled** strategy:

| Extension | Axis / meaning        | Use case              |
| --------- | --------------------- | --------------------- |
| `16.sem`  | Smallest-width based  | Toolbar, fixed rhythm |
| `16.hem`  | Height-based          | Vertical emphasis     |
| `16.wem`  | Width-based           | Horizontal emphasis   |

```kotlin
Text("Always same visual weight vs layout", fontSize = 14.sem)
```

---

## 🎛️ Facilitators — one value **normally**, another **in special cases**

**Idea:** Instead of `if (orientation == …)` everywhere, use **one call** that picks the right branch.

| Family        | Example (SCALED) | Plain English                                      |
| ------------- | ---------------- | -------------------------------------------------- |
| **Rotate**    | `80.sdpRotate(50, Orientation.LANDSCAPE)` | “Usually 80 sdp; in landscape use 50 (scaled).”      |
| **Mode**      | `30.sdpMode(200, UiModeType.TELEVISION)` | “Usually 30; on TV use 200 (scaled).”              |
| **Qualifier** | `60.sdpQualifier(120, DpQualifier.SMALL_WIDTH, 600)` | “If smallest width ≥ 600, use 120; else 60.”       |
| **Screen**    | `70.sdpScreen(150, UiModeType.TELEVISION, DpQualifier.SMALL_WIDTH, 600)` | Combines **mode** + **width threshold** in one rule. |

Many overloads accept **`Int`**, **`Dp`**, or **“plain”** variants — see the main README. **Other strategies** use the same **names** with their **prefix** (e.g. `asdpRotate`, `pwsdpMode`).

**Plain with `Dp` / `TextUnit` (no extra scaling):** When both the base and the alternate are **already** from the same strategy (e.g. `30.sdp` and `20.sdp`), use the overloads that take **`Dp`** or **`TextUnit`** as the alternate — only the branch runs:

```kotlin
val side = 30.sdp.sdpRotatePlain(20.sdp)
Text("Hi", fontSize = 16.ssp.sspRotatePlain(12.ssp))
```

**Nested facilitators:** You can chain them; the **effective order** is **how you nest** the expression. For **long** chains, keep using **`Dp` / `TextUnit` Plain** alternates.

---

## 🧱 Builders — `scaledDp()` / `scaledSp()` (chain many rules)

**Idea:** Start from a number and **chain** options: aspect ratio, ignore multi-window, then several **screen** rules. At the end read `.sdp`, `.hdp`, `.wdp` or Sp.

```kotlin
val padding = 16.scaledDp()
    .aspectRatio(true)
    .ignoreMultiWindows(true)
    .screen(UiModeType.TELEVISION, 40)
    .screen(DpQualifier.SMALL_WIDTH, 600, 24)
    .sdp
```

**Beginner tip:** Use simple **`16.sdp`** first; move to **builders** when one component needs **many** conditions.

---

## ✅ Checklist — “Did I miss something?”

| Topic              | Remember |
| ------------------ | -------- |
| **Resize**         | Inside `BoxWithConstraints`; “largest size that still fits” |
| **psdp / phdp / pwdp / pssp** | Percent strategy, same naming style as `sdp` / `hdp` / `wdp` / `ssp` |
| **…Px**            | `Float` pixels for Canvas / custom drawing |
| **sem / hem / wem** | Window-scaled Sp **without** following system font scale like `ssp` |
| **Rotate / Mode / Qualifier / Screen** | One-liner conditional sizing |
| **scaledDp / scaledSp** | Chain many rules, then `.sdp` / `.ssp` |
| **mm / cm / inch** | Real-world units → dp on screen |
| **Multiplatform**  | Same API on Android, desktop, iOS, macOS, web, Linux and Windows |

When in doubt, stay on **SCALED** (`sdp` / `hdp` / `wdp`) and add these tools only when a real layout problem appears.
