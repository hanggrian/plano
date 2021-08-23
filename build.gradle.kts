buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven(REPOSITORIES_OSSRH_SNAPSHOTS)
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(javafx)
        classpath(android)
        classpath(`git-publish`)
        classpath(hendraanggrian("generating-gradle-plugin", VERSION_PLUGIN_GENERATING))
        classpath(hendraanggrian("localization-gradle-plugin", VERSION_PLUGIN_LOCALIZATION))
        classpath(hendraanggrian("packaging-gradle-plugin", VERSION_PLUGIN_PACKAGING))

        classpath(hendraanggrian("javapoet-ktx", version = "0.1-SNAPSHOT"))
        classpath("com.badlogicgames.packr:packr:2.2-SNAPSHOT")
        classpath("com.google.gradle:osdetector-gradle-plugin:1.7.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(REPOSITORIES_OSSRH_SNAPSHOTS)
        maven("https://kotlin.bintray.com/kotlinx")
    }
}