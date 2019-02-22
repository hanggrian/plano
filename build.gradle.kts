buildscript {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(android())
        classpath(dokka())
        classpath(dokka("android"))
        classpath(hendraanggrian("r-gradle-plugin", "0.1"))
        classpath(hendraanggrian("buildconfig-gradle-plugin", "0.1"))
        classpath(hendraanggrian("packr-gradle-plugin", "0.1"))
        classpath(shadow())
        classpath(gitPublish())
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
    tasks {
        withType<Delete> {
            delete(files("out"))
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}

tasks {
    register<Delete>("clean") {
        delete(buildDir)
    }
    named<Wrapper>("wrapper") {
        gradleVersion = VERSION_GRADLE
    }
}