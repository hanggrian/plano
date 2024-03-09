val releaseGroup: String by project
val releaseArtifact: String by project
val releaseUrl: String by project

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("kapt") version libs.versions.kotlin
    application
    alias(libs.plugins.generating)
    alias(libs.plugins.packaging)
    alias(libs.plugins.ktlint)
}

application {
    applicationName = "Plano"
    mainClass.set("$releaseGroup.plano.PlanoApp")
}

packaging {
    icon.set(projectDir.resolve("logo_mac.icns"))
    verbose.set(true)
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    implementation(libs.core.assembler)
    implementation(project(":$releaseArtifact"))
    implementation(kotlin("reflect", libs.versions.kotlin.get()))
    implementation(libs.kotlinx.coroutines.javafx)
    implementation(libs.bundles.exposed)
    implementation(libs.sqlite.jdbc)
    implementation(libs.commons.lang3)
    implementation(libs.bundles.ktfx)
    implementation(libs.prefs.jvm)
    kapt(libs.prefs.compiler)
}

tasks {
    generateR {
        packageName.set("$releaseGroup.$releaseArtifact")
        resourcesDirectory.set(projectDir.resolve("src/main/resources"))
        css()
        this.properties {
            writeResourceBundle = true
        }
    }
    generateBuildConfig {
        packageName.set("$releaseGroup.$releaseArtifact")
        addField(String::class, "USER", "hendraanggrian")
        addField(String::class, "WEB", releaseUrl)
    }
}
