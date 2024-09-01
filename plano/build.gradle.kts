import java.util.Locale

val releaseArtifact: String by project
val distributionDebug: String by project

val jreVersion = JavaLanguageVersion.of(libs.versions.jre.get())

plugins {
    `java-library`
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.ktlint)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.localization)
}

kotlin.explicitApi()

dependencies {
    ktlintRuleset(libs.rulebook.ktlint)

    api(libs.bundles.ktor.client)

    implementation(libs.maven.artifact)
    implementation(libs.commons.math3)
    implementation(libs.bundles.slf4j)

    testImplementation(kotlin("test-junit", libs.versions.kotlin.get()))
    testImplementation(libs.truth)
}

buildConfig {
    className("BuildConfig2") // avoid clash with BuildConfig on app artifact
    useJavaOutput()
    buildConfigField<Boolean>("DEBUG", distributionDebug.toBoolean())
}

tasks {
    localizeJvm {
        resourceName.set("string")
        outputDirectory.set(rootDir.resolve("$releaseArtifact-javafx/src/main/resources"))
    }
    localizeAndroid {
        outputDirectory.set(rootDir.resolve("$releaseArtifact-android/src/main/res"))
    }

    compileJava {
        options.release = jreVersion.asInt()
    }
    compileTestJava {
        options.release = jreVersion.asInt()
    }
}

localization {
    defaultLocale.set(Locale.ENGLISH)
    importCsv(projectDir.resolve("locale.csv"))
}

tasks {
    localizeJvm {
        table.get().put("btn_show_directory", "en", "SHOW DIRECTORY")
        table.get().put("btn_show_directory", "id", "TUNJUKKAN FOLDER")
    }
    localizeAndroid {
        table.get().put("btn_show_image", "en", "SHOW IMAGE")
        table.get().put("btn_show_image", "id", "TUNJUKKAN GAMBAR")
    }
}
