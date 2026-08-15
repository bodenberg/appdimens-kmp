# Technical Performance Report: AppDimens Dynamic KMP — Comparative

This report documents the performance of the **AppDimens Dynamic KMP** library measured in **current test runs only** (2026-08-15) across the platforms the library targets. The benchmark harness is **BenchLab** (`benchlab` module) — a 3-way competitor comparison: **Dynamic 1.0.0** × **SDPS 3.1.6** × **Lib #2**.

> [!NOTE]
> **How to read the numbers**
>
> Every measurement was captured on **release** builds of the harness (Android: `minifyEnabled = true` + R8; Web: optimized wasm production build; Desktop: JVM release run). No comparisons against previous library versions or debug builds are included.

<p align="center">
  <img src="IMAGES/screenshot_benchmark.jpg" alt="Benchmark dashboard — AppDimens Dynamic" width="200" />
  &nbsp;
  <img src="IMAGES/screenshot_benchmark2.jpg" alt="Benchmark dashboard — additional capture" width="200" />
</p>

---

## 1. Benchmark Harness

The `benchlab` module runs **two independent benchmarks plus the legacy tests**, headlessly via the `AUTO_START` flag (intent extra on Android, JVM property on desktop/web, same flag on iOS/macOS entries):

- **Benchmark A — Compose API (main thread)**: Dynamic 1.0.0 × SDPS 3.1.6 × Lib #2 measured **together inside the same composition** — identical 20,000-iteration warm-up per library, **9 samples × 50,000 iterations** per sample, per-sample order rotation, anti-DCE checksums, two workloads (constant 1dp + mixed values), chunked at 5,000 ops per frame.
- **Benchmark B — Engine (`Dispatchers.Default`)**: Dynamic × SDPS only, off the main thread (Lib #2 has no non-Compose API → N/A).
- **Legacy T1/T2/T3**: original methodology (mean of 3 passes over 50,000-iteration timing cells), kept for continuity; px resolution values (1/10/100 dp, sdp + sdpa) are captured on every pass.

**Competitors:**

| Library | Version | Notes |
|---------|---------|-------|
| **Dynamic** | **1.0.0 (KMP)** | This library — measured on each target |
| SDPS | 3.1.6 | Legacy table-based Android artifact (Android-only) |
| Lib #2 | (KMP-compatible) | Per-call `@Composable` scaling (Android, JVM, iOS, wasmJs — no macOS variant) |

> **Fairness note:** the KMP BenchLab isolates Lib #2 behind an `expect/actual` bridge so the macOS target (which Lib #2 does not publish) still runs the full comparison with an equivalent formula (`min(w,h)/300`) — the bridge is byte-for-byte the same scaling rule on every platform.

---

## 2. Results by Platform (2026-08-15)

### 2.1 Android — physical device (Xiaomi 2107113SG, vili · Snapdragon 888-class · 2.84 GHz)

Release APK + R8 · sw=393dp · density 2.75.

**Benchmark B — Engine (off main thread), median per 1dp call:**

| Workload | Dynamic 1.0.0 | SDPS 3.1.6 |
| :--- | :---: | :---: |
| Constant 1dp | **~9.9 ns** | ~3.26 µs |
| Mixed (12 dims) | **~10 ns** | ~3.60 µs |

**Benchmark A — Compose probe (main thread, chunked), median per 1dp call:**

| Workload | Dynamic 1.0.0 | SDPS 3.1.6 | Lib #2 |
| :--- | :---: | :---: | :---: |
| Constant 1dp | **~27.7–29.7 ns** | ~5.27 µs | ~1.99–2.0 µs |
| Mixed (12 dims) | **~30.1 ns** | ~5.43 µs | ~1.93 µs |

**Legacy sdp (hot JIT, T3):**

| Library | T3 |
| :--- | :---: |
| **Dynamic 1.0.0** | **~7–10 ns** |
| SDPS 3.1.6 | ~3.28 µs |
| Lib #2 | ~1.11 µs |

> **Android ratios:** Dynamic is **~190× faster than SDPS** and **~70× faster than Lib #2** on the Compose-probe constant average (5,268/28; 1,988/28), **~330× faster than SDPS** on the off-main engine (3,260/9.9), and **~300× vs SDPS / ~100× vs Lib #2** on the legacy average (3,316/11; 1,114/11).

### 2.2 Desktop — JVM

Release JVM run · window 790×570 dp · same harness.

| Workload | Dynamic 1.0.0 | Lib #2 |
| :--- | :---: | :---: |
| Engine (per 1dp call) | **~3.0–3.3 ns** | ~320–400 ns |

> **Desktop ratio:** Dynamic is **~110× faster than Lib #2** (338/3.1). The JVM C2 compiler inlines the entire fast lane (one volatile load + identity compare + two multiplies) into the loop.

### 2.3 Web — wasmJs (browser)

Headless Chromium · viewport 1280×900 (sw=900dp, density 1.0) · optimized wasm production build.

| Workload | Dynamic 1.0.0 | Lib #2 |
| :--- | :---: | :---: |
| Engine (per 1dp call) | **~12–16 ns** | ~2,000–2,034 ns |

> **Web ratio:** Dynamic is **~140× faster than Lib #2** (2,017/14). Same kernels as Android/JVM compiled to wasm.

### 2.4 Native — iOS / macOS

The same harness compiles to Kotlin/Native. Lib #2 publishes Android, JVM, iOS and wasmJs (no macOS) — the bridge covers macOS. Native numbers are device-specific; the library's hot paths are identical C-free kernels (single float multiply over precomputed per-window factors).

---

## 3. Resolution Values (px) — deterministic, identical across tests

| dp | Dynamic 1.0.0 (sdp) | SDPS 3.1.6 (sdp) | Lib #2 (sdp) | Dynamic (sdpa) |
| :--- | :---: | :---: | :---: | :---: |
| **1dp** | 3.6025 | 3.6025 | 3.6025 | 3.7289135 |
| **10dp** | 36.025 | 36.0249 | 36.025 | 37.289135 |
| **100dp** | 360.25 | 360.25 | 360.25 | 372.89136 |

> All three libraries resolve the same px values on the same configuration — the difference is **speed**, not result. The KMP engine cross-check verifies bit-identical output vs the Android original for identical configurations.

---

## 4. How to Reproduce

```bash
# Android (device)
./gradlew :benchlab-android:installRelease
adb shell am start -n com.example.benchlab/.MainActivity --ez AUTO_START true
adb logcat -s BENCHLAB

# Desktop (JVM)
./gradlew :benchlab:run --args="--auto-start"

# Web (browser)
./gradlew :benchlab:wasmJsBrowserDistribution
# serve build/dist/wasmJs/productionExecutable and open in a browser

# Native
./gradlew :benchlab:linkDebugFrameworkIosArm64   # or macosArm64
```

Every phase, probe/engine medians, legacy cells and device info are logged to the console/logcat (`BENCHLAB` tag).

---

*Report Updated: 2026-08-15 · AppDimens Dynamic KMP 1.0.0 · BenchLab on Android (Xiaomi 2107113SG), JVM desktop and wasmJs browser · release builds · Dynamic is 70–330× faster than the competitors depending on platform and harness.*
