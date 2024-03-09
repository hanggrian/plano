pluginManagement.repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}
dependencyResolutionManagement.repositories {
    mavenCentral()
    google()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

rootProject.name = "plano"

include("plano", "plano-javafx", "plano-android")
include("website")
