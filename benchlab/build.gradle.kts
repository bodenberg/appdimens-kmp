import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/**
 * EN Test Lab (benchlab) — KMP module.
 *    Compares AppDimens Dynamic KMP (local `:library`) vs Lib #2
 *    (`network.chaintech:sdp-ssp-compose-multiplatform` — KMP artifact) on
 *    every platform: Android (consumed by `:benchlab-android`), Desktop (JVM),
 *    Web (Wasm), iOS and macOS. All benchmark logic + the Compose dashboard
 *    live in `commonMain`; each platform provides its own entry point.
 *
 * PT Test Lab (benchlab) — módulo KMP.
 *    Compara AppDimens Dynamic KMP (`:library` local) vs Lib #2
 *    (`network.chaintech:sdp-ssp-compose-multiplatform` — artefato KMP) em
 *    todas as plataformas: Android (consumido por `:benchlab-android`),
 *    Desktop (JVM), Web (Wasm), iOS e macOS. Toda a lógica de benchmark + o
 *    dashboard Compose ficam em `commonMain`; cada plataforma tem seu entry point.
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kmp.library)
}

// EN Desktop run/package entry (Compose Desktop JVM). `jvmMain` hosts the
//    `fun main()`; this block wires the `run` Gradle task so the test lab can
//    be launched on Windows/macOS/Linux (headless automation via AUTO_START).
// PT Entry run/package do Desktop (Compose Desktop JVM). O `jvmMain` hospeda o
//    `fun main()`; este bloco liga a task `run` do Gradle para o test lab ser
//    lançado em Windows/macOS/Linux (automação headless via AUTO_START).
compose.desktop {
    application {
        mainClass = "com.example.benchlab.MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
            )
            packageName = "BenchLab"
            packageVersion = "1.0.0"
        }
    }
}

kotlin {
    android {
        namespace = "com.example.benchlab.shared"
        compileSdk = 37
        minSdk = 24
    }

    jvm()

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "benchlab"
            isStatic = true
        }
    }
    // EN macOS IS enabled: the Lib #2 dependency is isolated behind the
    //    lib2SdpDp expect/actual bridge — the macOS actual replicates Lib #2's
    //    exact scaling math (its artifact publishes no macOS target), so the
    //    benchlab compiles and runs on every Compose Multiplatform target.
    // PT macOS está habilitado: a dependência da Lib #2 fica isolada atrás do
    //    bridge lib2SdpDp expect/actual — o actual do macOS replica a
    //    matemática exata da Lib #2 (o artefato dela não publica target macOS),
    //    então o benchlab compila e roda em todos os targets do Compose
    //    Multiplatform.
    macosArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "benchlab"
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":library"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            // Lib #2 — KMP artifact (Android/JVM/Wasm/iOS variants available).
            // Declared ONLY in source sets whose target publishes it: the
            // common code accesses it through the lib2SdpDp expect/actual
            // bridge, so macOS (no chaintech artifact) still compiles.
            implementation("network.chaintech:sdp-ssp-compose-multiplatform:1.0.7")
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.core)
            implementation("network.chaintech:sdp-ssp-compose-multiplatform:1.0.7")
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation("network.chaintech:sdp-ssp-compose-multiplatform:1.0.7")
        }
        iosMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation("network.chaintech:sdp-ssp-compose-multiplatform:1.0.7")
        }
        nativeMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
