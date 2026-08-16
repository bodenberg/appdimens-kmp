/**
 * Convention plugin that applies the standard KMP library configuration shared by
 * all AppDimens KMP modules (core + library-auto, library-density, etc.).
 *
 * Target matrix (mirrors what the underlying dependencies publish):
 *  - Full support (code API + Compose API): Android, JVM Desktop, iOS (arm64 +
 *    simulator arm64), macOS (arm64), Kotlin/JS (IR) and Wasm/JS.
 *  - Code-API only (no Compose UI — Compose Multiplatform 1.11 does not publish
 *    `ui`/`foundation` artifacts for these): Linux (x64 + arm64) and Windows
 *    (MinGW x64).
 *
 * Source-set hierarchy (default template + the `compose` group below):
 *  commonMain      — pure Kotlin (code API, cache, metrics). Compiles everywhere.
 *  composeMain     — Compose helpers (composition locals, remember plumbing, the
 *                    `compose/` extension families). Only Compose-capable targets
 *                    include it, so linux/mingw never resolve Compose artifacts.
 *  webMain         — default template group for js + wasmJs (browser actuals).
 *  composeTest     — auto-created test counterpart of composeMain: tests that
 *                    exercise the Compose layer (Density stamps etc.).
 *
 * NOTE: versions below mirror `gradle/libs.versions.toml` (Kotlin 2.4.10,
 * Compose 1.11.1, etc.). They are duplicated here because precompiled script
 * plugins in this build do not get `libs` catalog accessors generated.
 *
 * Usage in a module's build.gradle.kts:
 * ```
 * plugins {
 *     id("appdimens.kmp-library")
 *     alias(libs.plugins.vanniktech.maven.publish)
 * }
 * ```
 */
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.kotlin.multiplatform.library")
}

// EN Versions pinned to gradle/libs.versions.toml.
// PT Versões fixadas conforme gradle/libs.versions.toml.
private val coroutinesVersion = "1.10.2"
private val atomicfuVersion = "0.28.0"
private val composeVersion = "1.11.1"
private val material3Version = "1.9.0"
private val kotlinxBrowserVersion = "0.3.0"
private val androidxAnnotationVersion = "1.9.1"

kotlin {
    android {
        compileSdk = 37
        minSdk = 24
        withHostTest {}
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosArm64()
    iosSimulatorArm64()
    macosArm64()

    linuxX64()
    linuxArm64()
    mingwX64()

    // EN Classic Kotlin/JS (IR): nodejs() is enough for a library — consumers
    //    choose their own runtime; tests run under Node.
    // PT Kotlin/JS clássico (IR): nodejs() é suficiente para uma library —
    //    consumidores escolhem o runtime; testes rodam no Node.
    js {
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                // EN Select the browser for wasmJs tests via a Gradle property so the
                //    same repo runs on any machine: `-Pappdimens.wasmTestBrowser=firefox`
                //    (local Firefox), `=chrome` (default, CI ubuntu-latest has Chrome
                //    preinstalled) or `=safari`. The Karma runner is configured only
                //    here — a missing browser on a dev machine is a clear, single-point
                //    error instead of an implicit ChromeHeadless dependency.
                // PT Seleciona o browser dos testes wasmJs por propriedade Gradle para
                //    o mesmo repo rodar em qualquer máquina:
                //    `-Pappdimens.wasmTestBrowser=firefox` (Firefox local), `=chrome`
                //    (padrão, CI ubuntu-latest já tem Chrome) ou `=safari`.
                useKarma {
                    when (providers.gradleProperty("appdimens.wasmTestBrowser")
                        .orElse("chrome").get()) {
                        "firefox" -> useFirefoxHeadless()
                        "safari" -> useSafari()
                        else -> useChromeHeadless()
                    }
                }
            }
        }
    }

    // EN The default hierarchy template (common → native/web → apple/ios/macos/
    //    linux/mingw) stays as-is; we add one custom group: the Compose layer,
    //    shared by every target that has Compose Multiplatform artifacts.
    // PT O template padrão (common → native/web → apple/ios/macos/linux/mingw)
    //    permanece; adicionamos um grupo custom: a camada Compose, compartilhada
    //    por todos os targets com artefatos Compose Multiplatform.
    applyDefaultHierarchyTemplate {
        common {
            group("compose") {
                withAndroidTarget()
                withJvm()
                withIos()
                withMacos()
                withJs()
                withWasmJs()
            }
        }
    }

    sourceSets {
        // EN The platform actuals live in source sets that the template wires
        //    only to their default groups — not to the custom compose group
        //    (androidMain is created later by the AGP KMP plugin, so even
        //    `withAndroidTarget()` may not match it). Add same-tree edges so the
        //    Compose actuals see the composeMain expects.
        // PT Os actuals vivem em source sets que o template liga apenas aos seus
        //    grupos padrão — não ao grupo custom compose (androidMain é criado
        //    depois pelo plugin AGP KMP, então até `withAndroidTarget()` pode não
        //    casar). Arestas mesma-árvore para os actuals Compose enxergarem os
        //    expects do composeMain.
        getByName("androidMain") { dependsOn(getByName("composeMain")) }
        getByName("iosMain") { dependsOn(getByName("composeMain")) }
        getByName("macosMain") { dependsOn(getByName("composeMain")) }
        getByName("webMain") { dependsOn(getByName("composeMain")) }

        commonMain.dependencies {
            // EN Compose runtime publishes for EVERY target (incl. linux/mingw), so it
            //    lives in commonMain: the Compose compiler plugin (applied to all
            //    compilations) requires it on the class path even for targets whose
            //    hierarchy excludes the ui/foundation-backed composeMain layer.
            // PT O runtime do Compose publica para TODOS os targets (incl. linux/mingw),
            //    então fica no commonMain: o plugin do compilador Compose (aplicado a
            //    todas as compilações) exige o runtime no classpath mesmo para targets
            //    cuja hierarquia exclui a camada composeMain (ui/foundation).
            implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
        }
        getByName("composeMain").dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation("org.jetbrains.compose.ui:ui:$composeVersion")
            implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
            implementation("org.jetbrains.compose.material3:material3:$material3Version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation("androidx.annotation:annotation:$androidxAnnotationVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
        jvmMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
        iosMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
        nativeMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
        webMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-browser:$kotlinxBrowserVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        }
    }
}

// EN Silence the expect/actual Beta warning across all compilations (KT-61573).
// PT Silencia o aviso Beta de expect/actual em todas as compilações (KT-61573).
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}

// EN DimenPerformanceTest is a JVM/desktop-oriented micro-benchmark (see its KDoc):
//    on unoptimized Kotlin/Native debug binaries and interpreted JS/Wasm the
//    multi-million-iteration loops take tens of minutes or hang. Run it on JVM only;
//    other targets keep the functional suite (correctness, races, invalidation).
// PT DimenPerformanceTest é micro-benchmark orientado a JVM/desktop (ver KDoc): em
//    binários nativos debug e JS/Wasm interpretado os loops de milhões de iterações
//    levam dezenas de minutos ou travam. Roda só na JVM; os demais targets mantêm a
//    suíte funcional (correção, corridas, invalidação).
val slowBenchmarkClass = "com.appdimens.kmp.core.DimenPerformanceTest"

tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest>().configureEach {
    // EN Property access avoids the precompiled-script DSL resolving the `filter`
    //    function to CopySpec.filter. PT Acesso por propriedade evita o DSL do
    //    script pré-compilado resolver a função `filter` para CopySpec.filter.
    (this as org.gradle.api.tasks.testing.AbstractTestTask).filter
        .excludeTestsMatching(slowBenchmarkClass)
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest>().configureEach {
    (this as org.gradle.api.tasks.testing.AbstractTestTask).filter
        .excludeTestsMatching(slowBenchmarkClass)
}
