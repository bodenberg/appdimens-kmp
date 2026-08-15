// AppDimens KMP — BOM (java-platform) mirroring the Android original:
// version constraints for appdimens-kmp and appdimens-kmp-<strategy> modules.
plugins {
    `java-platform`
}

val libraryVersion: String =
    providers.gradleProperty("appdimens.version").orElse("1.0.0").get()

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
