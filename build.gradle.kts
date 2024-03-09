val developerId: String by project
val developerName: String by project
val developerUrl: String by project
val releaseGroup: String by project
val releaseArtifact: String by project
val releaseVersion: String by project
val releaseDescription: String by project
val releaseUrl: String by project

plugins {
    alias(libs.plugins.android.application) apply false
    kotlin("android") version libs.versions.kotlin apply false
    kotlin("android.extensions") version libs.versions.kotlin apply false
    kotlin("jvm") version libs.versions.kotlin apply false
    kotlin("kapt") version libs.versions.kotlin apply false
    alias(libs.plugins.generating) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    group = releaseGroup
    version = releaseVersion
}

subprojects {
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper>().configureEach {
        the<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>()
            .jvmToolchain(libs.versions.jdk.get().toInt())
    }
    plugins.withType<org.jlleitschuh.gradle.ktlint.KtlintPlugin>().configureEach {
        the<org.jlleitschuh.gradle.ktlint.KtlintExtension>()
            .version.set(libs.versions.ktlint.get())
    }
}

tasks.register(LifecycleBasePlugin.CLEAN_TASK_NAME) {
    delete(layout.buildDirectory)
}
