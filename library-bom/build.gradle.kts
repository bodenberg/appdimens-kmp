// AppDimens KMP — BOM (java-platform) mirroring the Android original:
// version constraints for appdimens-kmp and appdimens-kmp-<strategy> modules.
import com.vanniktech.maven.publish.JavaPlatform

plugins {
    `java-platform`
    // EN Same publishing plugin as the Android original (appdimens-dynamic):
    //    com.vanniktech.maven.publish, with configure(JavaPlatform()) below.
    // PT Mesmo plugin de publicação do Android original (appdimens-dynamic):
    //    com.vanniktech.maven.publish, com configure(JavaPlatform()) abaixo.
    alias(libs.plugins.vanniktech.maven.publish)
}

val libraryVersion: String =
    providers.gradleProperty("appdimens.version").orElse("1.0.1").get()

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("io.github.bodenberg:appdimens-kmp:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-auto:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-density:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-diagonal:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-fill:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-fit:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-fluid:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-interpolated:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-logarithmic:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-percent:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-perimeter:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-power:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-resize:$libraryVersion")
        api("io.github.bodenberg:appdimens-kmp-units:$libraryVersion")
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
mavenPublishing {
    coordinates("io.github.bodenberg", "appdimens-kmp-bom", libraryVersion)
    configure(JavaPlatform())

    pom {
        name.set("AppDimens KMP — BOM")
        description.set("Bill of Materials for AppDimens KMP — version constraints for appdimens-kmp and appdimens-kmp-<strategy> modules.")
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
