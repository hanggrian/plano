val releaseGroup: String by project
val releaseArtifact: String by project
val releaseVersion: String by project

plugins {
    alias(libs.plugins.android.application)
    kotlin("android") version libs.versions.kotlin
    kotlin("kapt") version libs.versions.kotlin
    alias(libs.plugins.ktlint)
}

android {
    namespace = "$releaseGroup.$releaseArtifact"
    testNamespace = "$namespace.test"
    compileSdk = libs.versions.sdk.target.get().toInt()
    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
        targetSdk = libs.versions.sdk.target.get().toInt()
        version = releaseVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        applicationId = namespace

        // custom fields
        buildConfigField("String", "VERSION_NAME", "\"$releaseVersion\"")
        buildConfigField("String", "NAME", "\"Plano\"")
        buildConfigField("String", "WEB", "\"$releaseVersion\"")
    }
    compileOptions {
        targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
    }
    kotlinOptions {
        jvmTarget = JavaVersion.toVersion(libs.versions.jdk.get()).toString()
    }
    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures.buildConfig = true
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    implementation(project(":$releaseArtifact"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.material)
    implementation(libs.flexbox)
    implementation(libs.bundles.androidx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.process.phoenix)
    debugImplementation(libs.leak.canary)
}
