pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// PREFER_PROJECT (not FAIL_ON_PROJECT_REPOS): the Kotlin wasm Node.js toolchain
// registers its own project repository (https://nodejs.org/dist) to download Node,
// and that repository must be consulted. No module declares repositories, so
// settings repositories below still apply everywhere.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    // EN Stable-only resolution: Compose Multiplatform 1.11.1 is published to Maven
    //    Central, so the Compose dev repository is NOT in the resolution path.
    // PT Resolução só com repositórios estáveis: Compose Multiplatform 1.11.1 está
    //    no Maven Central, então o repositório dev do Compose NÃO faz parte do path.
    repositories {
        google()
        mavenCentral()
    }
    // EN Expose the shared version catalog (`gradle/libs.versions.toml`) to ALL
    //    included builds so non-`build-logic` modules (library-bom and every
    //    library-* module) can read versions like `libs.versions.composeMultiplatform`
    //    without duplicating them as string literals. The `build-logic` included
    //    build declares the same catalog independently in
    //    `build-logic/settings.gradle.kts` — the two declarations are equivalent
    //    (Gradle does not unify catalogs across included builds; declaring the
    //    catalog twice keeps each build self-contained and avoids the
    //    "accessors not generated" runtime error).
    // PT Expõe o catálogo de versões compartilhado (`gradle/libs.versions.toml`)
    //    a TODOS os builds incluídos para que módulos fora do `build-logic`
    //    (library-bom e cada library-*) possam ler versões como
    //    `libs.versions.composeMultiplatform` sem duplicar como string. O
    //    `build-logic` declara o mesmo catálogo independentemente em
    //    `build-logic/settings.gradle.kts` — as duas declarações são
    //    equivalentes (o Gradle não unifica catálogos entre builds incluídos;
    //    declarar duas vezes mantém cada build autossuficiente e evita o erro
    //    "accessors not generated").
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

// EN npm-legal root name: the Kotlin wasmJs root aggregator derives the yarn
//    package name from it, and spaces are illegal in npm package names
//    ("AppDimens KMP" → `yarn install` fails with “Name contains illegal characters”).
// PT Nome raiz válido para npm: o agregador wasmJs do Kotlin deriva o nome do
//    pacote yarn a partir dele, e espaços são ilegais em nomes de pacote npm
//    ("AppDimens KMP" → `yarn install` falha com “Name contains illegal characters”).
rootProject.name = "appdimens-kmp"

includeBuild("build-logic")

include(":library")
include(":library-auto")
include(":library-density")
include(":library-diagonal")
include(":library-fill")
include(":library-fit")
include(":library-fluid")
include(":library-interpolated")
include(":library-logarithmic")
include(":library-percent")
include(":library-perimeter")
include(":library-power")
include(":library-resize")
include(":library-units")
include(":library-bom")
include(":benchlab")
include(":benchlab-android")
include(":app")
include(":app-android")