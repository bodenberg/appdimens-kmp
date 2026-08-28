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
 * Versioning model (Compose alignment — fixes the "consumer on a different
 * Compose version crashes the app" bug from 1.0.0/1.0.1):
 *  - The library is published with a **single pinned** Compose Multiplatform
 *    version (the one in `gradle/libs.versions.toml`). Bytecode is compiled
 *    against that version's API surface.
 *  - All Compose artifacts are declared as **`compileOnly`** in this convention
 *    plugin. We do **not** ship Compose into the consumer's runtime classpath;
 *    the consumer is expected to provide its own Compose at runtime. This
 *    prevents two stacked copies of the Compose runtime on the classpath
 *    (which is the most common source of the "module not found" crash
 *    consumers saw with 1.0.0/1.0.1).
 *  - We additionally publish **Gradle dependency constraints** (`require`) for
 *    the matching Compose version. When a consumer does not pin a Compose
 *    version of their own, Gradle resolves to ours. When the consumer pins a
 *    *different* (newer or older) version, Gradle's normal conflict resolution
 *    applies and the consumer's choice wins — but the runtime will then be
 *    ABI-mismatched, so the consumer must align. The README of each module
 *    states this contract.
 *  - The Compose runtime is the only Compose artifact we still need at compile
 *    time on the commonMain hierarchy (the Kotlin Compose compiler plugin
 *    requires it on the classpath of every compilation, even linux/mingw).
 *
 * NOTE on versions: this plugin reads Compose / Kotlin / Coroutines / AtomicFu
 * / kotlinx-browser / androidx.annotation directly from the shared version
 * catalog (`gradle/libs.versions.toml`) via the `libs` accessors generated for
 * the build-logic included build — so the source of truth is the catalog, not
 * duplicated string literals. (The 1.0.0/1.0.1 release hardcoded
 * `composeVersion = "1.11.1"` here, which silently drifted from the catalog
 * and made the misalignment bug worse to diagnose.)
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

// EN Versions resolved from the shared version catalog (gradle/libs.versions.toml).
//    build-logic declares the same catalog via `versionCatalogs { create("libs") { ... } }`
//    in build-logic/settings.gradle.kts, so the accessors below are real, not a
//    precompiled-script placeholder.
// PT Versões resolvidas a partir do catálogo de versões compartilhado
//    (gradle/libs.versions.toml). O build-logic declara o mesmo catálogo em
//    build-logic/settings.gradle.kts, então os acessores abaixo são reais.
private val coroutinesVersion = libs.versions.kotlinxCoroutines.get()
private val atomicfuVersion = libs.versions.kotlinxAtomicfu.get()
private val composeVersion = libs.versions.composeMultiplatform.get()
private val composeMaterial3Version = libs.versions.composeMaterial3.get()
private val kotlinxBrowserVersion = libs.versions.kotlinxBrowser.get()
private val androidxAnnotationVersion = libs.versions.androidx.annotation.get()

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
            //    Declared as `compileOnly` so we do NOT ship a second copy of the
            //    Compose runtime on the consumer's classpath at runtime — that double
            //    stacking was the root cause of the "module not found" / NoClassDef
            //    crashes users hit when their app was on a different Compose version
            //    (1.0.0/1.0.1). The matching constraint below nudges any consumer
            //    that didn't pin Compose to the version we compiled against.
            // PT O runtime do Compose publica para TODOS os targets (incl. linux/mingw),
            //    então fica no commonMain: o plugin do compilador Compose (aplicado a
            //    todas as compilações) exige o runtime no classpath mesmo para targets
            //    cuja hierarquia exclui a camada composeMain (ui/foundation).
            //    Declarado como `compileOnly` para NÃO embarcar uma segunda cópia do
            //    runtime do Compose no classpath do consumidor — essa duplicação era
            //    a causa raiz dos crashes "module not found" / NoClassDef quando o
            //    app estava numa versão diferente do Compose (1.0.0/1.0.1). A
            //    constraint abaixo alinha qualquer consumidor que não fixou versão.
            compileOnly("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")

            // EN Dependency constraints (consumer side) — these participate in the
            //    consumer's dependency graph but do NOT add the artifact to OUR
            //    publication. They pin the Compose version to the one we compiled
            //    against unless the consumer explicitly overrides it. Order of
            //    precedence (Gradle conflict resolution):
            //      1. Consumer `enforcedPlatform(...)` wins over anything.
            //      2. Consumer direct `require { strictly(...) }` wins.
            //      3. Highest non-strictly version in the graph wins.
            //    With these soft `require` constraints we say: "if no one in the
            //    graph pins Compose, please resolve to exactly this version so the
            //    consumer's runtime matches our compile-time ABI". A consumer on
            //    a deliberately different version will see a normal conflict
            //    resolution (their version wins) and is responsible for
            //    re-compiling our module against their Compose (or accepting the
            //    ABI drift documented in the README).
            // PT Constraints de dependência (lado consumidor) — entram no grafo
            //    do consumidor mas NÃO adicionam o artefato na NOSSA publicação.
            //    Fixam a versão do Compose na que compilamos, a menos que o
            //    consumidor sobrescreva explicitamente. Ordem de precedência
            //    (resolução de conflitos do Gradle):
            //      1. `enforcedPlatform(...)` do consumidor vence.
            //      2. `require { strictly(...) }` direto do consumidor vence.
            //      3. Maior versão não-strictly no grafo vence.
            //    Com estas constraints `require` soft dizemos: "se ninguém
            //    fixar Compose, resolva exatamente para esta versão para o
            //    runtime do consumidor bater com o ABI de tempo de compilação".
            //    Um consumidor com versão deliberadamente diferente verá a
            //    resolução normal (a dele vence) e é responsável por
            //    recompilar nosso módulo contra o Compose dele.
            constraints {
                add("composeRuntimeConsumer", "org.jetbrains.compose.runtime:runtime") {
                    version { require("$composeVersion") }
                    because("Library was compiled against Compose Multiplatform $composeVersion; mismatched runtime causes NoClassDefFoundError at app startup.")
                }
            }
        }
        getByName("composeMain").dependencies {
            // EN Same `compileOnly` strategy: the consumer MUST already have a
            //    matching Compose stack on the classpath (ui, foundation, and
            //    material3 for the helpers that touch M3 typography). The
            //    constraints below mirror the same alignment contract for the
            //    composeMain-only artifacts.
            // PT Mesma estratégia `compileOnly`: o consumidor JÁ precisa ter um
            //    stack Compose compatível no classpath (ui, foundation e
            //    material3 para os helpers que usam tipografia M3). As
            //    constraints abaixo espelham o mesmo contrato de alinhamento
            //    para os artefatos exclusivos do composeMain.
            compileOnly("org.jetbrains.compose.runtime:runtime:$composeVersion")
            compileOnly("org.jetbrains.compose.ui:ui:$composeVersion")
            compileOnly("org.jetbrains.compose.foundation:foundation:$composeVersion")
            compileOnly("org.jetbrains.compose.material3:material3:$composeMaterial3Version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")

            constraints {
                add("composeUiConsumer", "org.jetbrains.compose.ui:ui") {
                    version { require("$composeVersion") }
                    because("Library was compiled against Compose Multiplatform $composeVersion; mismatched ui causes NoSuchMethodError on internal calls.")
                }
                add("composeFoundationConsumer", "org.jetbrains.compose.foundation:foundation") {
                    version { require("$composeVersion") }
                    because("Library was compiled against Compose Multiplatform $composeVersion; mismatched foundation breaks layout/density stamps.")
                }
                add("composeMaterial3Consumer", "org.jetbrains.compose.material3:material3") {
                    version { require("$composeMaterial3Version") }
                    because("Library was compiled against Compose Material3 $composeMaterial3Version; mismatched M3 changes typography defaults.")
                }
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        // EN For module-level tests that touch the Compose layer (e.g. density-stamp
        //    tests) we add the matching Compose stack as `implementation` so the test
        //    runtime resolves it. This is INTERNAL to the test compilation — it does
        //    NOT flow to consumers, because `implementation` on a test source set
        //    produces test-classpath-only artifacts.
        //    The `composeTest` source set only exists in modules that declare a
        //    `composeTest` directory (Kotlin Gradle DSL creates it lazily); we
        //    therefore guard the lookup with `findByName` so modules without a
        //    `composeTest` directory still configure cleanly.
        // PT Para testes em nível de módulo que tocam a camada Compose (ex.: testes
        //    de carimbo de densidade) adicionamos o stack Compose como
        //    `implementation` para o runtime de teste resolver. Isto é INTERNO à
        //    compilação de testes — NÃO vaza para consumidores, porque
        //    `implementation` em test source set gera artefatos só do classpath
        //    de teste. O source set `composeTest` só existe em módulos que
        //    declaram um diretório `composeTest` (o Kotlin Gradle DSL cria sob
        //    demanda); por isso protegemos a busca com `findByName` para que
        //    módulos sem diretório `composeTest` continuem configurando
        //    corretamente.
        findByName("composeTest")?.dependencies {
            implementation("org.jetbrains.compose.ui:ui:$composeVersion")
            implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
            implementation("org.jetbrains.compose.material3:material3:$composeMaterial3Version")
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
