import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/**
 * EN Sample app — KMP module with a Compose Multiplatform demo of every
 *    AppDimens strategy (scaled, auto, density, diagonal, fill, fit, fluid,
 *    interpolated, logarithmic, percent, perimeter, power, resize) plus the
 *    DimenScaled builder and auto-resize. Runs on Android (consumed by
 *    `:app-android`), Desktop (JVM), Web (Wasm), iOS and macOS.
 * PT App sample — módulo KMP com um demo Compose Multiplatform de todas as
 *    estratégias AppDimens (scaled, auto, density, diagonal, fill, fit, fluid,
 *    interpolated, logarithmic, percent, perimeter, power, resize) além do
 *    builder DimenScaled e auto-resize. Roda em Android (consumido por
 *    `:app-android`), Desktop (JVM), Web (Wasm), iOS e macOS.
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kmp.library)
}

// EN Desktop run/package entry (Compose Desktop JVM). `jvmMain` hosts the
//    `fun main()`; this block wires the `run`/`package*` Gradle tasks so the
//    sample app can be launched and distributed on Windows/macOS/Linux.
// PT Entry run/package do Desktop (Compose Desktop JVM). O `jvmMain` hospeda o
//    `fun main()`; este bloco liga as tasks `run`/`package*` do Gradle para o
//    app sample ser lançado e distribuído em Windows/macOS/Linux.
compose.desktop {
    application {
        mainClass = "com.example.app.MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
            )
            packageName = "AppDimensDemo"
            packageVersion = "1.0.0"
        }
    }
}

kotlin {
    android {
        namespace = "com.example.app.shared"
        compileSdk = 37
        minSdk = 24
    }

    jvm()

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "appdimensDemo"
            isStatic = true
        }
    }
    macosArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "appdimensDemo"
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            // Core + scaled (library) and every opt-in satellite module.
            implementation(project(":library"))
            implementation(project(":library-auto"))
            implementation(project(":library-density"))
            implementation(project(":library-diagonal"))
            implementation(project(":library-fill"))
            implementation(project(":library-fit"))
            implementation(project(":library-fluid"))
            implementation(project(":library-interpolated"))
            implementation(project(":library-logarithmic"))
            implementation(project(":library-percent"))
            implementation(project(":library-perimeter"))
            implementation(project(":library-power"))
            implementation(project(":library-resize"))
            implementation(project(":library-units"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.core)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}
