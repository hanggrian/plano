buildscript {
    repositories {
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/hendraanggrian/packr")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", VERSION_KOTLIN))
        classpath(dokka())
        classpath(hendraanggrian("generating", "r-gradle-plugin", "0.5"))
        classpath(hendraanggrian("generating", "buildconfig-gradle-plugin", "0.5"))
        classpath(hendraanggrian("packr", "packr-gradle-plugin", "0.9"))
        classpath(shadow())
        classpath(gitPublish())
    }
}

allprojects {
    repositories {
        jcenter()
    }
    tasks {
        withType<Delete> {
            delete(projectDir.resolve("out"))
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