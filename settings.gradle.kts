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
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// PREFER_PROJECT (not FAIL_ON_PROJECT_REPOS): the Kotlin wasm Node.js toolchain
// registers its own project repository (https://nodejs.org/dist) to download Node,
// and that repository must be consulted. No module declares repositories, so
// settings repositories below still apply everywhere.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// EN npm-legal root name: the Kotlin wasmJs root aggregator derives the yarn
//    package name from it, and spaces are illegal in npm package names
//    ("AppDimens KMP" → `yarn install` fails with “Name contains illegal characters”).
// PT Nome raiz válido para npm: o agregador wasmJs do Kotlin deriva o nome do
//    pacote yarn a partir dele, e espaços são ilegais em nomes de pacote npm
//    ("AppDimens KMP" → `yarn install` falha com “Name contains illegal characters”).
rootProject.name = "appdimens-kmp"

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