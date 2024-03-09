import java.util.Locale

val releaseArtifact: String by project

plugins {
    `java-library`
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.ktlint)
    alias(libs.plugins.localization)
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    implementation(libs.bundles.ktor.client)
    implementation(libs.maven.artifact)
    implementation(libs.commons.math3)
    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(libs.truth)
}

tasks {
    localizeJvm {
        resourceName.set("string")
        outputDirectory.set(rootDir.resolve("$releaseArtifact-javafx/src/main/resources"))
    }
    localizeAndroid {
        outputDirectory.set(rootDir.resolve("$releaseArtifact-android/src/main/res"))
    }
}

localization {
    defaultLocale.set(Locale.ENGLISH)
    importCsv(projectDir.resolve("locale.csv"))
}
