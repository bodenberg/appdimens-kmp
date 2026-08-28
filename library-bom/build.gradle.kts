// AppDimens KMP — BOM (java-platform) mirroring the Android original:
// version constraints for appdimens-kmp and appdimens-kmp-<strategy> modules.
//
// EN The BOM also pins the **Compose Multiplatform** versions the library was
//    compiled against, so a consumer can do:
//      implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.2"))
//    in their version catalog and every `org.jetbrains.compose.*` artifact
//    resolves to the same version that the library's bytecode expects —
//    eliminating the "module not found" / NoClassDefFoundError crash that
//    occurred in 1.0.0/1.0.1 when consumers were on a different Compose
//    version. The Compose versions are read from the shared
//    `gradle/libs.versions.toml` so there is one source of truth.
//
// PT O BOM também fixa as versões do **Compose Multiplatform** contra as
//    quais a biblioteca foi compilada, então o consumidor pode usar:
//      implementation(platform("io.github.bodenberg:appdimens-kmp-bom:1.0.2"))
//    no catálogo de versões e cada artefato `org.jetbrains.compose.*` resolve
//    para a mesma versão que o bytecode da biblioteca espera — eliminando o
//    crash "module not found" / NoClassDefFoundError que ocorria em
//    1.0.0/1.0.1 quando consumidores estavam em outra versão do Compose. As
//    versões do Compose são lidas do `gradle/libs.versions.toml` para haver
//    uma única fonte de verdade.
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
    providers.gradleProperty("appdimens.version").orElse("1.0.2").get()

// EN Compose versions read from the shared version catalog. The BOM is the
//    recommended way for consumers to align Compose to AppDimens — it
//    guarantees the *same* Compose Multiplatform stack that the library was
//    compiled against, regardless of which version of Compose the rest of
//    the consumer's app would otherwise resolve.
// PT Versões do Compose lidas do catálogo compartilhado. O BOM é a forma
//    recomendada de o consumidor alinhar o Compose ao AppDimens — garante o
//    *mesmo* stack do Compose Multiplatform contra o qual a biblioteca foi
//    compilada, independentemente da versão de Compose que o resto do app
//    do consumidor resolveria.
val composeMultiplatformVersion: String = libs.versions.composeMultiplatform.get()
val composeMaterial3Version: String = libs.versions.composeMaterial3.get()

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // AppDimens modules
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

        // EN Compose Multiplatform alignment (the actual fix for the
        //    "different Compose version crashes the app" bug). Using
        //    `api(...)` constraints in a BOM makes them part of the
        //    consumer's *visible* constraint set, so any `org.jetbrains.compose.*`
        //    dependency in the consumer's project resolves to these versions
        //    unless the consumer explicitly overrides with `strictly` or
        //    `enforcedPlatform`.
        // PT Alinhamento do Compose Multiplatform (a correção real do bug
        //    "versão diferente do Compose crasha o app"). Usar constraints
        //    `api(...)` num BOM faz com que façam parte do conjunto de
        //    constraints *visíveis* do consumidor, então qualquer
        //    dependência `org.jetbrains.compose.*` no projeto do consumidor
        //    resolve para estas versões a menos que o consumidor sobrescreva
        //    explicitamente com `strictly` ou `enforcedPlatform`.
        api("org.jetbrains.compose.runtime:runtime:$composeMultiplatformVersion")
        api("org.jetbrains.compose.ui:ui:$composeMultiplatformVersion")
        api("org.jetbrains.compose.foundation:foundation:$composeMultiplatformVersion")
        api("org.jetbrains.compose.material3:material3:$composeMaterial3Version")
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
