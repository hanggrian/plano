import com.hendraanggrian.packr.PackrExtension

group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.packr")
    id("com.github.johnrengelman.shadow")
    application
}

application.mainClassName = "$RELEASE_GROUP.PlanoApplication"

sourceSets {
    getByName("main") {
        // manual import generated build
        val dirs = mutableListOf("src")
        val generatedDir = "plano/build/generated"
        if (rootDir.resolve(generatedDir).exists()) {
            dirs += "build/generated/r/src/main"
            dirs += "../$generatedDir/buildconfig/src/main"
            dirs += "../$generatedDir/r/src/main"
        }
        java.srcDirs(*dirs.toTypedArray())
        resources.srcDir("res")
    }
}

val configuration = configurations.register("ktlint")

dependencies {
    api(project(":$RELEASE_ARTIFACT"))
    api(kotlin("stdlib", VERSION_KOTLIN))

    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(hendraanggrian("ktfx", "ktfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))
    implementation(hendraanggrian("defaults", "defaults", VERSION_DEFAULTS))

    configuration {
        invoke(ktlint())
    }
}

tasks {
    val ktlint by registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(configuration.get())
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    "check" {
        dependsOn(ktlint.get())
    }
    register<JavaExec>("ktlintFormat") {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configuration.get())
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }

    named<com.hendraanggrian.r.RTask>("generateR") {
        resourcesDirectory = "res"
    }

    named<Jar>("jar") {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDir = buildDir.resolve("releases")
        baseName = RELEASE_ARTIFACT
        version = RELEASE_VERSION
        classifier = null
    }

    withType<com.hendraanggrian.packr.PackTask> {
        dependsOn("installDist")
    }
}

packr {
    mainClass = application.mainClassName
    executable = RELEASE_ARTIFACT
    classpath = files("build/install/$RELEASE_ARTIFACT-javafx/lib")
    resources = files("res")
    minimizeJre = PackrExtension.MINIMIZE_HARD
    macOS {
        name = "$RELEASE_NAME.app"
        icon = rootProject.projectDir.resolve("art/$RELEASE_ARTIFACT.icns")
        bundleId = RELEASE_GROUP
    }
    windows32 {
        name = RELEASE_NAME
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_202-x86"
    }
    verbose = true
    openOnDone = true
}