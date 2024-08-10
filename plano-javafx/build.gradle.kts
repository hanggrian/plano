import java.nio.charset.StandardCharsets
import java.util.Properties

val developerName: String by project
val releaseGroup: String by project
val releaseVersion: String by project
val releaseArtifact: String by project
val releaseUrl: String by project

plugins {
    alias(libs.plugins.javafx)
    kotlin("jvm") version libs.versions.kotlin
    application
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.packaging)
    alias(libs.plugins.ktlint)
}
buildscript {
    dependencies.classpath(libs.ph.css)
}

javafx {
    version = "${libs.versions.jdk.get()}.0.9"
    modules("javafx.controls", "javafx.swing", "javafx.web")
}

application {
    applicationName = "Plano"
    mainClass.set("$releaseGroup.$releaseArtifact.PlanoApp")
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
}

buildConfig {
    useJavaOutput()
    buildConfigField("GROUP", "$releaseGroup.$releaseArtifact")
    buildConfigField("VERSION", releaseVersion)
    buildConfigField("NAME", application.applicationName)
    buildConfigField("USER", developerName)
    buildConfigField("WEB", releaseUrl)
}

val r = buildConfig.forClass("R")
val generateR by tasks.registering {
    val resources = sourceSets["main"].resources.asFileTree
    inputs.files(resources)
    doFirst {
        resources.visit {
            path
                .lowercase()
                .replace("\\W".toRegex(), "_")
                .replace("_${file.extension}", "")
                .let { key ->
                    if (key !in r.buildConfigFields.map { it.name }) {
                        r.buildConfigField(key, "/$path")
                    }
                }
            when (file.extension) {
                "properties" ->
                    file
                        .inputStream()
                        .use { stream ->
                            val properties = Properties().apply { load(stream) }
                            properties.keys
                                .forEach { value ->
                                    val key = "string_$value"
                                    if (key !in r.buildConfigFields.map { it.name }) {
                                        r.buildConfigField(key, value.toString())
                                    }
                                }
                        }
                "css" ->
                    com.helger.css.reader.CSSReader
                        .readFromFile(
                            file,
                            StandardCharsets.UTF_8,
                            com.helger.css.ECSSVersion.CSS30,
                        )!!
                        .allStyleRules
                        .flatMap { it.allSelectors }
                        .mapNotNull {
                            val member =
                                it
                                    .getMemberAtIndex(0)
                                    ?.asCSSString
                                    ?: return@mapNotNull null
                            when {
                                member.startsWith('.') -> member.substringAfter('.')
                                member.startsWith('#') -> member.substringAfter('#')
                                else -> member
                            }
                        }.forEach { member ->
                            val key = "style_${member.replace('-', '_')}"
                            if (key !in r.buildConfigFields.map { it.name }) {
                                r.buildConfigField(key, member)
                            }
                        }
            }
        }
    }
}
tasks.generateBuildConfig {
    dependsOn(generateR)
}
