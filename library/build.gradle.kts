import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.kmp.library)
    // EN Same publishing plugin as the Android original (appdimens-dynamic).
    // PT Mesmo plugin de publicação do Android original (appdimens-dynamic).
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "com.appdimens.kmp"
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

// EN ── Maven Central publishing (same plugin as the Android original) ────────
//    com.vanniktech.maven.publish, declared in the plugins {} block above.
//    Publishing runs through the Maven Central Portal (central.sonatype.com);
//    Sonatype OSSRH was shut down in June 2025. Credentials are read from
//    ~/.gradle/gradle.properties (never committed):
//      mavenCentralUsername=<central-portal-user-token-username>
//      mavenCentralPassword=<central-portal-user-token-password>
//      signing.keyId=<8-hex-char-gpg-key-id>
//      signing.password=<gpg-passphrase>
//      signing.secretKeyRingFile=/absolute/path/to/secring.gpg
//    Tasks: publishToMavenLocal | publishToMavenCentral | publishAndReleaseToMavenCentral
// PT ── Publicação no Maven Central (mesmo plugin do Android original) ────────
//    O plugin publica pelo Maven Central Portal (central.sonatype.com); o OSSRH
//    foi desligado em junho/2025. Credenciais vêm de ~/.gradle/gradle.properties
//    (nunca commitadas).
val libraryVersion: String =
    providers.gradleProperty("appdimens.version").orElse("1.0.0").get()

mavenPublishing {
    coordinates("io.github.bodenberg", "appdimens-kmp", libraryVersion)

    pom {
        name.set("AppDimens KMP — Core + Scaled")
        description.set("AppDimens KMP core: shared snapshot-partitioned cache and plumbing plus the default scaled strategy (sdp/hdp/wdp/ssp). Kotlin Multiplatform — Android, JVM, iOS, macOS and Web.")
        url.set("https://github.com/bodenberg/appdimens-kmp")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("bodenberg")
                name.set("Jean Bodenberg")
                email.set("jean.bodenberg2@outlook.com")
            }
        }

        scm {
            connection.set("scm:git:github.com/bodenberg/appdimens-kmp.git")
            developerConnection.set("scm:git:ssh://github.com/bodenberg/appdimens-kmp.git")
            url.set("https://github.com/bodenberg/appdimens-kmp")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/bodenberg/appdimens-kmp/issues")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
