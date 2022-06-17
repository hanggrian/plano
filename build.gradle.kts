buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven(REPOSITORIES_OSSRH_SNAPSHOTS)
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(javafx)
        classpath(android)
        classpath(`git-publish`)
        //classpath(hendraanggrian("generating-gradle-plugin", VERSION_PLUGIN_GENERATING))
        //classpath(hendraanggrian("localization-gradle-plugin", VERSION_PLUGIN_LOCALIZATION))
        //classpath(hendraanggrian("packaging-gradle-plugin", VERSION_PLUGIN_PACKAGING))
        classpath("com.hendraanggrian:javapoet-ktx:0.1-SNAPSHOT")
        classpath("com.opencsv:opencsv:5.5.2")
        classpath("com.helger:ph-css:6.4.0")
        classpath(files("generating-gradle-plugin-0.2.jar"))
        classpath(files("localization-gradle-plugin-0.2.jar"))
        classpath(files("packaging-gradle-plugin-0.2.jar"))
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