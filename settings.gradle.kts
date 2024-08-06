pluginManagement.repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
dependencyResolutionManagement.repositories {
    mavenCentral()
    google()
}

rootProject.name = "plano"

include("plano", "plano-javafx", "plano-android")
include("website")
