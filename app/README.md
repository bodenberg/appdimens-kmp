# AppDimens Demo (KMP)

Sample application demonstrating **AppDimens Dynamic KMP** on every supported platform.

## Targets

| Target | Entry point |
|--------|-------------|
| **JVM desktop** | `jvmMain/kotlin/com/example/app/main.kt` (`application { mainClass = … }`) |
| **iOS** | `iosMain/kotlin/com/example/app/MainViewController.kt` (`MainViewController()` exported to the framework) |
| **macOS** | `macosMain/kotlin/com/example/app/main.kt` (native `Window` + NSApplication run loop) |
| **Web / Kotlin/JS** | `jsMain/kotlin/com/example/app/main.kt` (browser) |
| **Web / wasmJs** | `wasmJsMain/kotlin/com/example/app/main.kt` (browser) |
| **Android** | see the `app-android` module (Android-only variant) |

## Run

```bash
./gradlew :app:run                 # desktop (JVM)
./gradlew :app:wasmJsBrowserRun    # web
./gradlew :app:linkDebugFrameworkIosArm64   # iOS framework for Xcode
./gradlew :app:linkDebugExecutableMacosArm64 # macOS binary
```

## Content

- `com/example/app/compose/SdpDemoScreen.kt` — interactive SDP/HDP/WDP demo with sliders and auto-resize examples.
- `com/example/app/compose/DemoCalcRouting.kt` — demo routing.

The `app-android` module is the Android-only variant (release builds with R8 + `proguard-rules.pro`).

See the [main README](../README.md) for the library documentation.
