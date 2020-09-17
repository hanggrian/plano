import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(android())
        classpath(hendraanggrian("r-gradle-plugin", VERSION_PLUGIN_R))
        classpath(hendraanggrian("buildconfig-gradle-plugin", VERSION_PLUGIN_BUILDCONFIG))
        classpath(hendraanggrian("locale-gradle-plugin", VERSION_PLUGIN_LOCALE))
        classpath(hendraanggrian("packr-gradle-plugin", VERSION_PLUGIN_PACKR))
        classpath(shadow())
        classpath(gitPublish())
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }
    tasks {
        withType<Delete> { delete(files("out")) }
        withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }
    }
}

tasks.register<Delete>("clean") { delete(buildDir) }
