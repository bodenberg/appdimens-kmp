import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kmp.library)
}

kotlin {
    android {
        namespace = "com.appdimens.kmp.units"
        compileSdk = 37
        minSdk = 24
        withHostTest {}
        // EN Pre-shrink + pre-optimize the AAR at build time (mirrors the Android
        //    original's `isMinifyEnabled = true` on release). See proguard-rules.pro.
        // PT Pré-encolhe + pré-otimiza o AAR no build (espelha o release do Android
        //    original). Ver proguard-rules.pro.
        optimization {
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
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":library"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.atomicfu)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.annotation)
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
