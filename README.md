# AppDimens Dynamic — KMP

## Responsive `dp` / `sp` for **Android · Desktop (JVM) · iOS · macOS · Web (JS + Wasm) · Linux · Windows** — Jetpack Compose Multiplatform and Kotlin APIs

<p align="center">
  <a href="https://github.com/bodenberg/appdimens-kmp/releases" title="Releases">
    <img src="https://img.shields.io/badge/version-1.0.1-blue.svg" alt="Version 1.0.1">
  </a>
  &nbsp;
  <a href="LICENSE" title="Apache License 2.0">
    <img src="https://img.shields.io/badge/license-Apache%202.0-green.svg" alt="License Apache 2.0">
  </a>
  &nbsp;
  <a href="https://kotlinlang.org/docs/multiplatform.html" title="Kotlin Multiplatform">
    <img src="https://img.shields.io/badge/KMP-Kotlin%20Multiplatform-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin Multiplatform">
  </a>
  &nbsp;
  <img src="https://img.shields.io/badge/Android%20%7C%20JVM%20%7C%20iOS%20%7C%20macOS%20%7C%20Web%20%7C%20Linux%20%7C%20Windows-3DDC84.svg?logo=android&logoColor=white" alt="Platforms">
  &nbsp;
  <img src="https://img.shields.io/badge/Kotlin-2.x-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin">
  &nbsp;
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-4285F4.svg?logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform">
  &nbsp;
  <a href="./DOCUMENTATION/README.md" title="Scaling strategies and modules">
    <img src="https://img.shields.io/badge/scaling%20modes-14-orange.svg" alt="14 scaling modes">
  </a>
</p>

<p align="center">
  <a href="./GUIDE-FOR-BEGINNERS.md" title="Step-by-step guide for beginners">
    <img src="https://img.shields.io/badge/Beginner%20guide-Step%20by%20step-f59e0b?style=for-the-badge&logo=readthedocs&logoColor=white&labelColor=b45309" alt="Beginner guide">
  </a>
  &nbsp;&nbsp;
  <a href="./DOCUMENTATION/README.md" title="Strategies, formulas, and when to use each scaling mode">
    <img src="https://img.shields.io/badge/Docs-Full%20documentation-1d4ed8?style=for-the-badge&logo=gitbook&logoColor=white&labelColor=1e3a8a" alt="Full documentation">
  </a>
  &nbsp;&nbsp;
  <a href="./DOCUMENTATION/index.md" title="API documentation — package index">
    <img src="https://img.shields.io/badge/API%20DOCUMENTATION--7F52FF?style=for-the-badge&logo=markdown&logoColor=white&labelColor=4c1d95" alt="API documentation">
  </a>
</p>

<p align="center">
  <a href="./PERFORMANCE.md" title="Technical performance report — all platforms">
    <img src="https://img.shields.io/badge/Performance-Report-0f766e?style=for-the-badge&logo=google-analytics&logoColor=white&labelColor=134e4a" alt="Performance report">
  </a>
  &nbsp;&nbsp;
  <a href="./PERFORMANCE-COMPARATIVE.md" title="Performance comparison vs competitor libraries">
    <img src="https://img.shields.io/badge/Performance-Comparative-7c3aed?style=for-the-badge&logo=speedtest&logoColor=white&labelColor=5b21b6" alt="Performance comparative">
  </a>
  &nbsp;&nbsp;
  <a href="./R8-PROGUARD.md" title="R8 full mode and ProGuard rules — library, consumer, app">
    <img src="https://img.shields.io/badge/R8%20%26%20ProGuard-Rules-334155?style=for-the-badge&logo=android&logoColor=white&labelColor=1e293b" alt="R8 and ProGuard rules">
  </a>
</p>

---

![AppDimens Banner](IMAGES/banner_top.png)

Write values like `16.sdp` and the library scales them from the current window **Configuration** (size, density, optional flags) — on **every platform**: Android, JVM desktop, iOS, macOS, the browser (Kotlin/JS + WebAssembly), **Linux** and **Windows**.

**New here?** Use **Quick start** below, then [**GUIDE-FOR-BEGINNERS**](./GUIDE-FOR-BEGINNERS) for every strategy in plain language.

**Documentation:** [DOCUMENTATION/README.md](DOCUMENTATION/README.md) · [DOCUMENTATION/MODULES.md](DOCUMENTATION/MODULES.md) · [PRD](DOCUMENTATION/PRD.md) · [PDR](DOCUMENTATION/PDR.md) · [Mathematics](DOCUMENTATION/MATHEMATICS-AND-CALCULUS.md) · [Changelog](CHANGELOG.md)

---

## Supported platforms (1.0.1)

| Target | Compose API | `code` API | Notes |
|--------|:---:|:---:|-------|
| **Android** (min SDK 24) | ✅ | ✅ | Full Jetpack Compose integration, `Context`-wrapping window handle, foldables via `androidx.window`, event-driven config watcher (`ComponentCallbacks2`) |
| **JVM desktop** | ✅ | ✅ | Compose Desktop (AWT window) — live window configuration, resize-aware |
| **iOS** (`iosArm64`, `iosSimulatorArm64`) | ✅ | ✅ | Compose iOS (`UIScreen`-derived window handle) |
| **macOS** (`macosArm64`) | ✅ | ✅ | Compose native macOS (`NSScreen`-derived window handle) |
| **Web / Kotlin/JS** (`js`, IR) | ✅ | ✅ | Compose for Web (browser) — live viewport size and density |
| **Web / wasmJs** | ✅ | ✅ | Compose for Web (browser) — live viewport size and density |
| **Linux** (`linuxX64`, `linuxArm64`) | — | ✅ | Native Kotlin — `code` API only. No windowing API in the Kotlin/Native stdlib, so `defaultPlatformContext()` returns `null` (build an `AppDimensContext` from a `ScreenConfiguration` or use the `DimenMetrics` overloads). Compose Multiplatform does not publish `ui`/`foundation` for Linux native |
| **Windows** (`mingwX64`) | — | ✅ | Native Kotlin — `code` API only (same `defaultPlatformContext() = null` contract as Linux; no Win32 bindings in the stdlib, no Compose `ui` artifacts for MinGW) |

> **Intel Apple (`iosX64` / `macosX64`), tvOS and watchOS are not in the matrix**: Compose
> Multiplatform 1.11 publishes no `ui`/`foundation` artifacts for those targets, so the
> Compose layer cannot compile there (and the code API alone would fragment the
> per-platform handle contract). The supported Apple targets are exactly the ones the
> ecosystem publishes.

Every platform shares the **same Kotlin API**, the same **cache** and the same **math kernels**. The `code` (non-Compose) APIs take a platform-neutral **`AppDimensContext`** window handle; on Android it wraps the platform `Context` (auto-cached per raw Context).

**This is a faithful Kotlin Multiplatform port of the Android library** (`appdimens-kmp`): same packages, same extension names, same formulas, same bit-identical precision — plus the desktop, iOS, macOS, web, Linux and Windows targets.

---

## Installation (v1.0.1)

**1.0.1** keeps the modular packaging: the library ships as a **principal** artifact (`common` + `core` + **scaled** + **plain**) plus optional strategy modules. Kotlin packages and imports are unchanged.

### With BOM (common to all platforms)

```kotlin
// commonMain.dependencies (or the platform source set you target)
implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.1"))

implementation("io.github.bodenberg:appdimens-kmp")

implementation("io.github.bodenberg:appdimens-kmp-percent")
implementation("io.github.bodenberg:appdimens-kmp-power")
implementation("io.github.bodenberg:appdimens-kmp-fluid")
implementation("io.github.bodenberg:appdimens-kmp-auto")
implementation("io.github.bodenberg:appdimens-kmp-density")
implementation("io.github.bodenberg:appdimens-kmp-diagonal")
implementation("io.github.bodenberg:appdimens-kmp-fill")
implementation("io.github.bodenberg:appdimens-kmp-fit")
implementation("io.github.bodenberg:appdimens-kmp-interpolated")
implementation("io.github.bodenberg:appdimens-kmp-logarithmic")
implementation("io.github.bodenberg:appdimens-kmp-perimeter")
implementation("io.github.bodenberg:appdimens-kmp-resize")
implementation("io.github.bodenberg:appdimens-kmp-units")
```

### Missing strategy module

If you import `com.appdimens.kmp.compose.<strategy>` (or `code.<strategy>`) without adding the matching artifact, the Gradle check `checkAppDimensModules` fails with a line such as:

```text
Missing AppDimens module for import …percent… — add: implementation("io.github.bodenberg:appdimens-kmp-percent:1.0.1")
```

Runtime helper: `com.appdimens.kmp.core.MissingModule` (package → Maven coordinate). Version comes from the `appdimens.version` Gradle property.

### Without BOM

```kotlin
implementation("io.github.bodenberg:appdimens-kmp:1.0.1")
implementation("io.github.bodenberg:appdimens-kmp-percent:1.0.1")
// same satellites as above, each with :1.0.1
```

### Artifact matrix

| Maven artifact | Contents |
|----------------|----------|
| `appdimens-kmp` | `common`, `core`, `code.plain`, `code` / `compose` **scaled** |
| `appdimens-kmp-<strategy>` | `code.<strategy>` + `compose.<strategy>` |
| `appdimens-kmp-bom` | Version constraints (`java-platform`) |

Module graph: [DOCUMENTATION/MODULES.md](DOCUMENTATION/MODULES.md).

**Requirements:** Min SDK **24** (Android) · **Kotlin 2.x** & **Java 17** (JVM) · **Compose Multiplatform 1.x**

---

## Quick start — Scaled (Compose Multiplatform)

```kotlin
import com.appdimens.kmp.compose.*

Box(
    Modifier
        .padding(16.sdp)
        .width(100.wdp)
        .height(48.hdp)
) {
    Text("Hello", fontSize = 16.ssp)
}
```

This exact code runs unchanged on **Android, desktop, iOS, macOS and the browser (JS + Wasm)** — the extensions read the live per-window configuration automatically.

| Extension | Based on | Typical use |
|-----------|----------|-------------|
| **`sdp`** | Smallest window width | Padding, margins |
| **`hdp`** | Window height | Row height |
| **`wdp`** | Window width | Column width |
| **`ssp`** | Same idea as `sdp`, for text | `fontSize` |
| **`sem`** | Same idea as `sdp`, for text | `fontSize ignore system font scale` |

---

## Compose — setup before advanced APIs

**If you only use `sdp` / `hdp` / `wdp` / `ssp` / `hsp` / `wsp` / `sem` / `hem` / `wem` (and variants like `sdpa`), you can skip this block.**

### `AppDimensProvider`

Use it when you call **`.sdpMode`**, **`.sdpScreen`**, **`.sspMode`**, **`.sspScreen`**, or similar **facilitators** that depend on **UI mode / fold state**. It sets `LocalUiModeType` once for the tree instead of resolving mode on every call, and provides `LocalDimenMetrics` — a coherent per-window snapshot that every `rememberDimen*` helper uses.

```kotlin
import com.appdimens.kmp.core.AppDimensProvider

setContent {
    AppDimensProvider {
        MyApp()
    }
}
```

`AppDimensProvider` works on **all Compose-capable platforms**. Outside Android (desktop, web, iOS, macOS) it builds the window context from the **live window configuration**, so a resize or rotation creates a new snapshot and every resolution self-heals — the same guarantee the Android `Context` provides natively. (Linux/Windows native expose the `code` API only.)

### `DimenCache.invalidateOnConfigChange`

Since **1.0.0** the cache is **partitioned per window snapshot** (`DimenMetrics`): every resolution is keyed by the exact configuration it was computed for, so a rotated, resized, or recreated window can never read a stale value. Explicit invalidation is therefore **not required for correctness** — this API is retained as a compatibility hook and no longer wipes other windows’ hot entries.

Call it when the **same Activity stays alive** across **rotation, split-screen, or density/font changes** and you want to refresh internal bookkeeping. If the Activity is **recreated** on config change (default), you don’t need it. Details: [library/PERFORMANCE.md](library/PERFORMANCE.md).

```kotlin
import com.appdimens.kmp.core.DimenCache

override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    DimenCache.invalidateOnConfigChange(newConfig)
}
```

---

## Compose — next steps

### Suffixes (`a`, `i`, `ia`)

| Suffix | Meaning |
|--------|---------|
| *(none)* | Default |
| **`a`** | Aspect ratio–aware curve |
| **`i`** | Ignore multi-window heuristic (may return unscaled base when it triggers) |
| **`ia`** | Both |

```kotlin
16.sdpa      // + aspect ratio
32.hdpi      // height axis + ignore multi-window
16.sspa      // scalable sp + aspect ratio
```

### More text styles

```kotlin
Text("Scaled (sw)", fontSize = 16.ssp)
Text("Scaled (height)", fontSize = 20.hsp)
Text("Scaled (width)", fontSize = 18.wsp)
Text("No system font scale (sw)", fontSize = 16.sem)   // sem / hem / wem
```

### Orientation inverters (examples)

```kotlin
32.sdpPh   // SW-based; in portrait uses height
32.sdpLw   // SW-based; in landscape uses width
50.hdpLw   // Height-based; in landscape uses width
50.wdpLh   // Width-based; in landscape uses height
```

### Facilitators (after `AppDimensProvider` if you use mode/screen)

```kotlin
import com.appdimens.kmp.compose.*
import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType

80.sdpRotate(50, orientation = Orientation.LANDSCAPE)
30.sdpMode(200, UiModeType.TELEVISION)
60.sdpQualifier(120, DpQualifier.SMALL_WIDTH, 600)
16.sspRotate(24, orientation = Orientation.LANDSCAPE)
```

Full catalog: [DOCUMENTATION/COMPOSE-API-CONVENTIONS.md](DOCUMENTATION/COMPOSE-API-CONVENTIONS.md).

### Builders (`scaledDp` / `scaledSp`)

```kotlin
val pad = 16.scaledDp()
    .aspectRatio(true)
    .screen(UiModeType.TELEVISION, 40)
    .screen(DpQualifier.SMALL_WIDTH, 600, 24)
    .sdp
```

### Auto-resize (inside `BoxWithConstraints`)

Picks the **largest** font or size in a **min…max** range that still **fits** the space. Use for titles, squares, etc.

```kotlin
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.appdimens.kmp.compose.resize.autoResizeTextSp

BoxWithConstraints(Modifier.fillMaxWidth()) {
    val fontSize = autoResizeTextSp(
        text = "Headline that must fit",
        minSp = 12,
        maxSp = 28,
        stepSp = 1,
        maxLines = 2,
    )
    Text("Headline that must fit", fontSize = fontSize, maxLines = 2)
}
```

More APIs (`autoResizeSquareSize`, `ResizeBound`, …): [DOCUMENTATION/resize.md](DOCUMENTATION/resize.md).

---

## Kotlin (non-Compose / `code` API)

All non-Compose APIs take a platform-neutral **`AppDimensContext`** window handle. Inside a Composable you can obtain it from `localAppDimensContext()` (provided by `AppDimensProvider`); on Android you can also wrap a raw `Context` via the platform adapter.

```kotlin
import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.code.DimenSdp
import com.appdimens.kmp.code.DimenSsp

// Inside composition (all platforms):
val appContext = localAppDimensContext()!!

val paddingPx = DimenSdp.sdp(appContext, 16)
val heightPx = DimenSdp.hdp(appContext, 32)
val widthPx = DimenSdp.wdp(appContext, 100)
val fontPx = DimenSsp.ssp(appContext, 16)

// Extensions (see code package)
// 16.ssp(appContext), DimenSdp.scaled(16).screen(...).sdp(appContext), sdpRotate, …
```

The same `code` APIs run on **every target** — JVM desktop, iOS, macOS, js/wasmJs, Linux and Windows. On Compose-capable platforms `localAppDimensContext()` resolves the live window/screen handle (AWT window, `UIScreen`, `NSScreen`, browser viewport); on Linux/Windows native build your own `AppDimensContext` from a `ScreenConfiguration` (or use the `DimenMetrics` overloads directly).

---

## Physical units (mm, cm, inch)

Approximate **real-world** size on screen (density-based). Compose: use helpers from the library and **`.dp`** on the result where needed — see [DOCUMENTATION/physical-units.md](DOCUMENTATION/physical-units.md). Code module: `com.appdimens.kmp.code.units.DimenPhysicalUnits` (`toDpFromMm`, …).

---

<p align="center">
  <img src="IMAGES/screenshot.jpg" alt="Layout example" width="200"/>
  &nbsp;
  <img src="IMAGES/screenshot_benchmark.jpg" alt="Benchmark" width="200" />
</p>

---

## More strategies & full API

**Recommendation order for most apps:** **Scaled** (with or without `a`) → then **percent** → then **auto**; explore the rest when you have a clear need (fluid, fit, diagonal, etc.).

Other strategies (**percent**, **power**, **fluid**, **auto**, **diagonal**, **fill**, **fit**, **interpolated**, **logarithmic**, **perimeter**, **density**, **resize**, **units**) mirror the Scaled suffix patterns under a different import prefix and ship as separate Maven modules. See [DOCUMENTATION/MODULES.md](DOCUMENTATION/MODULES.md), [DOCUMENTATION/README.md](DOCUMENTATION/README.md), and [GUIDE-FOR-BEGINNERS](./GUIDE-FOR-BEGINNERS).

| Resource | Use for |
|----------|---------|
| [DOCUMENTATION/README.md](DOCUMENTATION/README.md) | Per-strategy explanations |
| [DOCUMENTATION/MODULES.md](DOCUMENTATION/MODULES.md) | Gradle/Maven module graph (1.0.1) |
| [COMPOSE-API-CONVENTIONS.md](DOCUMENTATION/COMPOSE-API-CONVENTIONS.md) | Every Compose property & facilitator (scaled catalog + prefix map) |
| [DOCUMENTATION/index.md](DOCUMENTATION/index.md) | Package index |

**Example apps:** `app` (sample, all platforms) and `benchlab` (competitor benchmark) — see [app](app/README.md) and [benchlab](benchlab/README.md).

---

## Optional: cache & performance

- Results are cached in **`DimenCache`** — lock-free, **partitioned per window/configuration snapshot** (no disk persistence since 1.0.0).
- Some paths **skip** storing in the snapshot cache when a cheap multiply is enough — see [library/PERFORMANCE.md](library/PERFORMANCE.md).
- **Batch / low-level keys:** not needed for normal app code; library extensions already use the cache.

---

**Scaled** uses **300 dp** as the design reference. It is the **most widely used** strategy in real apps and the **recommended default**: use plain `sdp` / `hdp` / `wdp` / `ssp` when a single curve is enough, and the **`a`** suffix (aspect ratio–aware), e.g. `16.sdpa`, when you want scaling tuned to screen shape. **After Scaled**, the next strategies teams typically adopt are **percent** (sizes as a fraction of an axis) and **auto** (breakpoint-style steps); the other modes are for specialized layouts — see [DOCUMENTATION/README.md](DOCUMENTATION/README.md).

---

**Facilitators — two “Plain” styles:** `*RotatePlain`, `*ModePlain`, `*QualifierPlain`, `*ScreenPlain` (and `*PlainPx`) exist with the alternate as **`Number`** (active branch still runs through scaling/cache) or as **`Dp` / `TextUnit`** (only the condition is evaluated; **no** second scaling). For **nested** chains such as `30.sdp.sdpRotatePlain(20.sdp).sdpModePlain(40.sdp, UiModeType.TELEVISION)`, prefer **`Dp` / `TextUnit`** alternates so neither the receiver nor the alternate is scaled twice. **Nesting order** is the order you write the chain (outer → inner). That is **different** from **`DimenScaled` `.screen` chains**, where **priority is defined inside the builder API**, not by lexical nesting — see [DOCUMENTATION/COMPOSE-API-CONVENTIONS.md](DOCUMENTATION/COMPOSE-API-CONVENTIONS.md).

---

**Views / `code`:** the same **logic-only** Plain branching exists on **`Float` px** + **`AppDimensContext`** — `Dimen*PlainPx.kt` per strategy (e.g. `psdpRotatePlainPx` in `com.appdimens.kmp.code.percent`), with shared helpers in **`com.appdimens.kmp.code.plain`** (`DimenPlainBranch.kt`). **Dp/Sp facilitator** sources use the same **`Dimen<Strategy>DpExtensions.kt` / `Dimen<Strategy>SpExtensions.kt`** names as in `compose/<strategy>/` (scaled: `DimenSdpExtensions.kt` / `DimenSspExtensions.kt` under `code/scaled/`). Details in [DOCUMENTATION/COMPOSE-API-CONVENTIONS.md](DOCUMENTATION/COMPOSE-API-CONVENTIONS.md) §4.5 and [DOCUMENTATION/README.md](DOCUMENTATION/README.md).

---

## Highlights (1.0.1)

- Code-only scaling (no XML dimen grids) · **SDP / HDP / WDP** + **14** scaling modes  \
- **Aspect ratio** & **multi-window** flags · **Inverters** & **facilitators** · **Foldable** awareness on Android via WindowManager  \
- **Physical units** · **Resize** helpers · **DimenScaled** chains  \
- **Kotlin Multiplatform**: identical API and math on Android, JVM, iOS, macOS, js/wasmJs, Linux and Windows  \
- **Fast lane on every platform**: Compose reads one CompositionLocal + one multiply; `code` reads one volatile slot + identity compare + two multiplies  \
- **Resize-aware on every platform**: desktop/web/iOS/macOS providers rebuild the snapshot from the live window configuration  \
- **AARs pre-shrunk with R8** at build time (all 14 modules) — consumers get optimized bytecode even in debug  \

### What's New in 1.0.0 (KMP port)

| Change | Description |
|--------|-------------|
| **Full KMP port** | Same packages/API/math as the Android original, compiled for Android, JVM, iOS (`iosArm64`/`iosSimulatorArm64`), macOS (`macosArm64`), Kotlin/JS, wasmJs (browser), Linux (`linuxX64`/`linuxArm64`, `code` API) and Windows (`mingwX64`, `code` API). |
| **Platform-neutral `AppDimensContext`** | Non-Compose APIs take a window handle instead of an Android `Context`; per-platform adapters (Android `Context`, AWT window, `UIScreen`, `NSScreen`, browser viewport). |
| **Event-driven config watcher** | Android keeps the `ComponentCallbacks2` listener; desktop/web/iOS/macOS rebuild the snapshot on live window configuration. |
| **Specialized kernels** | `resolveSdpPx`, `resolveSdpDp`, `resolveSdpaPx`, `resolveSdpaDp`, `resolveHdpPx`, `resolveHdpDp`, `resolveWdpPx`, `resolveWdpDp` — one kernel per family/qualifier, zero branches, volatile load + identity compare + legacy multiply order. |
| **`fastMetricsForCode`** | Non-Compose fast-lane resolution: skips the ThreadLocal probe entirely — one volatile load, one identity compare, two float multiplies on the hit path. |
| **Compose fast lane** | Reads `metricsScope ?: LocalDimenMetrics` (1 CompositionLocal + 1 multiply) — no `LocalContext` read, works on every platform. |
| **DimenMetrics eager computation** | `normalizedAspectRatio` / `logNormalizedAspectRatio` are plain `val` — no hidden `synchronized` probe in the SDPA fast lane. |
| **R8 pre-shrink for all AARs** | All 14 library modules ship R8-optimized bytecode (`optimization { minify = true }` + `-optimizationpasses 10` + `-allowaccessmodification`), mirroring the Android release builds. |
| **BenchLab KMP** | Competitor benchmark (Dynamic vs SDPS vs Lib #2) that runs on Android, JVM, iOS, macOS and the browser. |

### What's New in 1.0.1 (audit fixes + full target matrix)

| Change | Description |
|--------|-------------|
| **`fastPartition` race fixed** | The fast partition was two independent atomics (`fastPartition` + `fastPartitionMetrics`); an interleaving could pair a snapshot partition with another window's metrics and return a wrong dimension under concurrency. Now a single atomic `FastPartitionSlot(metrics, partition)` publishes both as one coherent state. |
| **Android context cache cycle fixed** | `WeakHashMap<Context, AndroidAppDimensContext>` was neutralized because the value held the key strongly. The value is now a `WeakReference`, so Activities/Contexts are collectable again. |
| **Configuration listeners now disposable** | `registerConfigurationListener` returns a `ConfigurationRegistration` with `dispose()`; the Android registry unregisters `ComponentCallbacks` and drops per-app listener sets when the last listener is removed — no more accumulator of live contexts. |
| **Weak identity map fixed (JVM/Android)** | `WeakHashMap<WeakKey<K>, V>` made the wrapper itself the weak key — the GC could drop live entries while the key was strongly reachable. Replaced with a strong `HashMap` of `IdentityWeakReference` wrappers + `ReferenceQueue`: entries live exactly as long as their referent. |
| **Native metrics scope is thread-local** | The Kotlin/Native `MetricsScopeHolder` is now `@ThreadLocal` — each worker gets its own slot, so two workers can never cross snapshots (was one shared mutable global). |
| **Native/Web identity maps use `===`** | `SynchronizedIdentityMap` / `WebIdentityMap` were backed by `LinkedHashMap` (equals-based); two distinct but equal window handles collapsed. They now compare by identity, matching JVM/Android. |
| **Config-watcher lifecycle (no Activity retention)** | The registered listener is context-free (no value→key cycle), the watcher is reference-counted (`acquireConfigWatcher` / `releaseConfigWatcher`), the Android `AppDimensProvider` pairs them via `DisposableEffect`, and the Android registry holds listeners weakly — a destroyed Activity is collectable even without an explicit `dispose()`. |
| **Strict race tests** | `DimenCacheRaceTest` now requires the exact expected value per key/snapshot (no longer accepts “any valid value”); a wrong transient read is counted even if a later peek was correct. New GC / identity / lifecycle / native-worker tests added. |
| **Apple CI gate corrected** | The `verify-apple` workflow no longer references the nonexistent `linkDebugFrameworkIosSimulatorArm64` task (this library publishes KMP Maven variants, not Apple frameworks); the compile steps remain the Apple gates. |
| **Full KMP target matrix** | Added classic **Kotlin/JS** (`js`, IR), **Linux** (`linuxX64` + `linuxArm64`) and **Windows** (`mingwX64`) targets; every module is built from a shared `appdimens.kmp-library` convention plugin. |
| **Encapsulated diagnostics API** | `DimenCache.isInitialized` is now a plain `Boolean` and `cacheStats()` returns an immutable `CacheStats` — the experimental atomics are `internal`, keeping the public ABI clean. |
| **Restored CI** | `verify-linux` (Linux + JVM + JS + wasm tests) and `verify-apple` (macOS runner compiling `iosSimulatorArm64` / `macosArm64`) re-added as release gates. |
| **Distribution hardening** | `distributionSha256Sum` pinned in the Gradle wrapper; the Compose dev repository was removed from `settings.gradle.kts`; LICENSE (Apache-2.0) and signed Maven publishing with sources are configured. |

---

*Apache License 2.0 — responsive layout utilities for Kotlin Multiplatform (Android · JVM · iOS · macOS · Web · Linux · Windows).*
