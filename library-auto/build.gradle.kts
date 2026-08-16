/**
 * AppDimens KMP — Auto strategy module.
 *
 * The KMP target matrix and source-set hierarchy come from the
 * `appdimens.kmp-library` convention plugin; this file only adds what is specific
 * to the module: Android namespace + R8 consumer contract, the dependency on
 * `:library` (core + scaled) and the Maven Central publishing coordinates.
 */
plugins {
    id("appdimens.kmp-library")
    // EN Same publishing plugin as the Android original (appdimens-dynamic).
    // PT Mesmo plugin de publicação do Android original (appdimens-dynamic).
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "com.appdimens.kmp.auto"
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

    sourceSets {
        commonMain.dependencies {
            api(project(":library"))
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
    providers.gradleProperty("appdimens.version").orElse("1.0.1").get()

mavenPublishing {
    coordinates("io.github.bodenberg", "appdimens-kmp-auto", libraryVersion)

    pom {
        name.set("AppDimens KMP — Auto")
        description.set("Auto strategy: automatic selection of the best strategy based on the current screen configuration.")
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
