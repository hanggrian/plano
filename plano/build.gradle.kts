import java.util.Locale

plugins {
    `java-library`
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.localization)
}

dependencies {
    ktlint(libs.ktlint, ::configureKtlint)
    ktlint(libs.rulebook.ktlint)
    implementation(libs.bundles.ktor.client)
    implementation(libs.maven.artifact)
    implementation(libs.commons.math3)
    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(libs.truth)
}

tasks {
    localizeJvm {
        resourceName.set("string")
        outputDirectory.set(rootDir.resolve("$RELEASE_ARTIFACT-javafx/src/main/resources"))
    }
    localizeAndroid {
        outputDirectory.set(rootDir.resolve("$RELEASE_ARTIFACT-android/src/main/res"))
    }
}

localization {
    defaultLocale.set(Locale.ENGLISH)
    importCsv(projectDir.resolve("locale.csv"))
}
