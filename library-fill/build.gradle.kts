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
        namespace = "com.appdimens.kmp.fill"
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
    coordinates("io.github.bodenberg", "appdimens-kmp-fill", libraryVersion)

    pom {
        name.set("AppDimens KMP — Fill")
        description.set("Fill strategy: fills the available space using viewport-relative sizing.")
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
