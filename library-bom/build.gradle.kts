// AppDimens KMP — BOM (java-platform) mirroring the Android original:
// version constraints for appdimens-dynamic and appdimens-dynamic-<strategy> modules.
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
        api("io.github.bodenberg:appdimens-dynamic:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-auto:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-density:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-diagonal:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-fill:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-fit:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-fluid:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-interpolated:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-logarithmic:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-percent:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-perimeter:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-power:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-resize:$libraryVersion")
        api("io.github.bodenberg:appdimens-dynamic-units:$libraryVersion")
    }
}
