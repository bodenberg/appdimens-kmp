import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    android {
        namespace = "com.appdimens.dynamic"
        compileSdk = 37
        minSdk = 24
        withHostTest {}
        // EN Ship the same R8 consumer contract as the Android original: keep the public
        //    API names (code/compose/common), the cache-key enum members, kotlin.Metadata,
        //    the ResizeBound sealed hierarchy and the ScreenFactors padding fields — see
        //    consumer-rules.pro. Without these, consumer R8 passes may rename enum entries
        //    (silent scaling errors from ordinal-encoded cache keys) and strip the ARM64
        //    false-sharing padding from the multi-core hot path.
        // PT Embarque o mesmo contrato R8 de consumo do Android original — ver
        //    consumer-rules.pro. Sem isto, o R8 do app consumidor pode renomear entradas
        //    de enum (erros silenciosos de escala por chaves com ordinal) e remover o
        //    padding anti false-sharing do caminho quente multi-core.
        optimization {
            // EN Pre-shrink + pre-optimize the AAR at build time (mirrors the Android
            //    original's `isMinifyEnabled = true` on release): consumers get R8-
            //    optimized bytecode even before their own pass. See proguard-rules.pro.
            // PT Pré-encolhe + pré-otimiza o AAR no build (espelha o `isMinifyEnabled = true`
            //    do release no Android original): consumidores recebem bytecode otimizado
            //    pelo R8 antes mesmo do próprio passo. Ver proguard-rules.pro.
            minify = true
            keepRules.files("proguard-rules.pro")
            consumerKeepRules.files("consumer-rules.pro")
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        // EN Compose-based tests need a browser environment (skiko.wasm cannot be
        //    loaded from plain Node), so library tests run via the browser test task.
        // PT Testes baseados em Compose precisam de ambiente de navegador (skiko.wasm
        //    não carrega em Node puro), então os testes da biblioteca rodam via browser.
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.atomicfu)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.window)
            implementation(libs.kotlinx.coroutines.core)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        iosMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        nativeMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}