plugins {
    android("application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdk = SDK_TARGET
    defaultConfig {
        minSdk = SDK_MIN
        targetSdk = SDK_TARGET
        multiDexEnabled = true
        applicationId = RELEASE_GROUP
        buildConfigField("String", "VERSION_NAME", "\"$RELEASE_VERSION\"")
        buildConfigField("String", "NAME", "\"Plano\"")
        buildConfigField("String", "WEB", "\"$RELEASE_GITHUB\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        named("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDir("src")
            res.srcDir("res")
            resources.srcDir("src")
        }
    }
    buildTypes {
        named("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    lint {
        isAbortOnError = false
    }
    packagingOptions {
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

ktlint()

dependencies {
    api(project(":$RELEASE_ARTIFACT"))
    api(kotlinx("coroutines-android", VERSION_COROUTINES))
    implementation(hendraanggrian("auto", "prefs-android", VERSION_PREFS))
    kapt(hendraanggrian("auto", "prefs-compiler", VERSION_PREFS))
    implementation(hendraanggrian("auto", "bundles", VERSION_BUNDLES))
    kapt(hendraanggrian("auto", "bundles-compiler", VERSION_BUNDLES))
    implementation(androidx("multidex", version = VERSION_MULTIDEX))
    implementation(androidx("lifecycle", "lifecycle-extensions", VERSION_LIFECYCLE))
    implementation(androidx("lifecycle", "lifecycle-viewmodel-ktx", VERSION_LIFECYCLE))
    implementation(androidx("lifecycle", "lifecycle-livedata-ktx", VERSION_LIFECYCLE))
    implementation(androidx("core", "core-ktx"))
    implementation(androidx("appcompat"))
    implementation(androidx("preference", version = "1.1.1"))
    implementation(androidx("coordinatorlayout", version = "1.1.0"))
    implementation(androidx("recyclerview", version = "1.2.1"))
    implementation(androidx("room", "room-ktx", VERSION_ROOM))
    kapt(androidx("room", "room-compiler", VERSION_ROOM))
    implementation(material())
    implementation(processPhoenix())
    debugImplementation(leakCanary())
}