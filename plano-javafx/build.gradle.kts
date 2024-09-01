import com.helger.css.ECSSVersion
import com.helger.css.reader.CSSReader
import java.nio.charset.StandardCharsets
import java.util.Properties

val developerName: String by project
val releaseGroup: String by project
val releaseVersion: String by project
val releaseArtifact: String by project
val releaseUrl: String by project
val distributionName: String by project

val jdkVersion = JavaLanguageVersion.of(libs.versions.jdk.get())
val jreVersion = JavaLanguageVersion.of(libs.versions.jre.get())

val javaModules = listOf("java.sql", "jdk.crypto.ec")
val javafxModules = listOf("javafx.controls", "javafx.graphics", "javafx.swing")
val javaArguments = listOf("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED")

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
    version = libs.versions.javafx.get()
    modules = javafxModules
}

application {
    applicationName = distributionName
    mainClass.set("$releaseGroup.$releaseArtifact.PlanoApp")
    applicationDefaultJvmArgs = javaArguments
}

packaging {
    modules.set(javaModules + javafxModules)
    javaArgs.set(javaArguments)
    verbose.set(true)
    windows {
        modulePaths.set(listOf(File("C:/JavaFX/javafx-jmods-${libs.versions.javafx.get()}")))
        icon.set(projectDir.resolve("icons/$distributionName.ico"))
    }
    mac {
        modulePaths.set(listOf(File("/Library/JavaFX/javafx-jmods-${libs.versions.javafx.get()}")))
        icon.set(projectDir.resolve("icons/$distributionName.icns"))
    }
}

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

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
    buildConfigField("NAME", distributionName)
    buildConfigField("USER", developerName)
    buildConfigField("WEB", releaseUrl)
}

tasks {
    compileJava {
        options.release = jreVersion.asInt()
    }
    test {
        jvmArgs(*javaArguments.toTypedArray())
    }

    val r = buildConfig.forClass("R")
    val generateR by registering {
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
                                Properties()
                                    .apply { load(stream) }
                                    .keys
                                    .forEach { value ->
                                        val key = "string_$value"
                                        if (key !in r.buildConfigFields.map { it.name }) {
                                            r.buildConfigField(key, value.toString())
                                        }
                                    }
                            }
                    "css" ->
                        CSSReader
                            .readFromFile(file, StandardCharsets.UTF_8, ECSSVersion.CSS30)!!
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
    generateBuildConfig {
        dependsOn(generateR)
    }
}
