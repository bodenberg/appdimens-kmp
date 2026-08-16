/**
 * AppDimens KMP — Interpolated strategy module.
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
        namespace = "com.appdimens.kmp.interpolated"
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
//    com.vanniktech.maven.publish; credentials in ~/.gradle/gradle.properties.
//    Tasks: publishToMavenLocal | publishToMavenCentral | publishAndReleaseToMavenCentral
// PT ── Publicação no Maven Central (mesmo plugin do Android original) ────────
//    Credenciais em ~/.gradle/gradle.properties (nunca commitadas).
val libraryVersion: String =
    providers.gradleProperty("appdimens.version").orElse("1.0.1").get()

mavenPublishing {
    coordinates("io.github.bodenberg", "appdimens-kmp-interpolated", libraryVersion)

    pom {
        name.set("AppDimens KMP — Interpolated")
        description.set("Interpolated strategy: smooth interpolation between design-time sizes across screen widths.")
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
