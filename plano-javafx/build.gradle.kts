plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("kapt") version libs.versions.kotlin
    application
    alias(libs.plugins.generating)
    alias(libs.plugins.packaging)
}

application {
    applicationName = "Plano"
    mainClass.set("$RELEASE_GROUP.plano.PlanoApp")
}

packaging {
    icon.set(projectDir.resolve("logo_mac.icns"))
    verbose.set(true)
}

dependencies {
    ktlint(libs.ktlint, ::configureKtlint)
    ktlint(libs.rulebook.ktlint)
    implementation(project(":$RELEASE_ARTIFACT"))
    implementation(kotlin("reflect", libs.versions.kotlin.get()))
    implementation(libs.kotlinx.coroutines.javafx)
    implementation(libs.bundles.exposed)
    implementation(libs.sqlite.jdbc)
    implementation(libs.bundles.ktfx)
    implementation(libs.prefs.jvm)
    kapt(libs.prefs.compiler)
}

tasks {
    generateR {
        packageName.set("$RELEASE_GROUP.$RELEASE_ARTIFACT")
        resourcesDirectory.set(projectDir.resolve("res"))
        css()
        // properties {
        //     writeResourceBundle.set(true)
        // }
    }
    generateBuildConfig {
        packageName.set("$RELEASE_GROUP.$RELEASE_ARTIFACT")
        addField("DEBUG", RELEASE_DEBUG)
        addField("USER", "hendraanggrian")
        addField("WEB", RELEASE_URL)
    }
}
