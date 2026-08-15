/**
 * EN Android entry point of the sample app. Thin `com.android.application`
 *    module hosting the Activity; the demo UI lives in the KMP `:app` module
 *    (commonMain). AGP 9 requires a separate application module because KMP +
 *    `com.android.application` cannot share a subproject.
 * PT Entry point Android do app sample. Módulo fino `com.android.application`
 *    que hospeda a Activity; a UI do demo vive no módulo KMP `:app`
 *    (commonMain). O AGP 9 exige módulo de aplicação separado porque KMP +
 *    `com.android.application` não podem compartilhar o mesmo subprojeto.
 */
plugins {
    // AGP is already on the classpath via the KMP library plugin, so no version here.
    id("com.android.application")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    val keystoreFile = rootProject.file("test_keystore.jks")

    val createTestKeystore by tasks.registering(Exec::class) {
        onlyIf { !keystoreFile.exists() }
        val keytoolBin = File(System.getProperty("java.home"), "bin/keytool")
        commandLine(
            if (keytoolBin.exists()) keytoolBin.absolutePath else "keytool",
            "-genkeypair", "-v",
            "-keystore", keystoreFile.absolutePath,
            "-storetype", "PKCS12",
            "-alias", "test",
            "-keyalg", "RSA", "-keysize", "2048", "-validity", "10000",
            "-storepass", "123456", "-keypass", "123456",
            "-dname", "CN=AppDimens KMP CI, OU=CI, O=AppDimens, L=Unspecified, ST=Unspecified, C=BR"
        )
        outputs.file(keystoreFile)
    }
    tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(createTestKeystore)
    }

    signingConfigs {
        create("sample") {
            storeFile = keystoreFile
            storePassword = System.getenv("SAMPLE_STORE_PASSWORD") ?: "123456"
            keyAlias = "test"
            keyPassword = System.getenv("SAMPLE_KEY_PASSWORD") ?: "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("sample")
        }
        debug {
            signingConfig = signingConfigs.getByName("sample")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // KMP sample core (demo UI) — Android target.
    implementation(project(":app"))
    // Core + scaled (library) and every opt-in satellite module.
    implementation(project(":library"))
    implementation(project(":library-auto"))
    implementation(project(":library-density"))
    implementation(project(":library-diagonal"))
    implementation(project(":library-fill"))
    implementation(project(":library-fit"))
    implementation(project(":library-fluid"))
    implementation(project(":library-interpolated"))
    implementation(project(":library-logarithmic"))
    implementation(project(":library-percent"))
    implementation(project(":library-perimeter"))
    implementation(project(":library-power"))
    implementation(project(":library-resize"))
    implementation(project(":library-units"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.kotlinx.coroutines.core)
}
