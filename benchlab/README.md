# BenchLab (KMP)

Competitor benchmark for **AppDimens Dynamic KMP**: measures **Dynamic 1.0.1** vs **SDPS 3.1.6** vs **Lib #2** on every supported platform with identical methodology (same composition, identical warm-up, 9 samples × 50,000 iterations, per-sample order rotation, anti-DCE checksums, chunked at 5,000 ops/frame).

## Targets

| Target | Entry point |
|--------|-------------|
| **Android** | `benchlab-android` module (release APK + R8) |
| **JVM desktop** | `jvmMain/kotlin/com/example/benchlab/main.kt` |
| **iOS** | `iosMain/kotlin/com/example/benchlab/MainViewController.kt` |
| **macOS** | `macosMain/kotlin/com/example/benchlab/main.kt` |
| **Web / wasmJs** | `wasmJsMain/kotlin/com/example/benchlab/main.kt` |

Lib #2 is isolated behind an `expect/actual` bridge (`Lib2Bridge`) so the **macOS** target — which Lib #2 does not publish — still runs the full comparison with an equivalent formula (`min(w,h)/300`, byte-for-byte the same rule on every platform).

## Run (headless)

```bash
# Android (device)
./gradlew :benchlab-android:installRelease
adb shell am start -n com.example.benchlab/.MainActivity --ez AUTO_START true
adb logcat -s BENCHLAB

# Desktop (JVM)
./gradlew :benchlab:run --args="--auto-start"

# Web
./gradlew :benchlab:wasmJsBrowserDistribution
# serve build/dist/wasmJs/productionExecutable, open in a browser, results print to console

# Native frameworks
./gradlew :benchlab:linkDebugFrameworkIosArm64 :benchlab:linkDebugExecutableMacosArm64
```

Results (probe medians, engine medians, legacy T1/T2/T3, device info) are printed to the console / logcat (`BENCHLAB` tag). Latest numbers: [PERFORMANCE.md](../PERFORMANCE.md) · [PERFORMANCE-COMPARATIVE.md](../PERFORMANCE-COMPARATIVE.md).
