group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.generating.r")
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
    implementation(hendraanggrian("ktfx", version = VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    configuration {
        invoke(ktlint())
    }
}

tasks {
    val ktlint = register("ktlint", JavaExec::class) {
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
    register("ktlintFormat", JavaExec::class) {
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

    named<com.hendraanggrian.generating.r.RTask>("generateR") {
        resourcesDirectory = file("res")
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
    classpath("$buildDir/install/$RELEASE_ARTIFACT/lib")
    resources("$projectDir/res")
    vmArgs("Xmx2G")
    macOS {
        name = "$RELEASE_NAME.app"
        icon = "${rootProject.projectDir}/art/$RELEASE_ARTIFACT.icns"
        bundleId = RELEASE_GROUP
    }
    windows64 {
        name = RELEASE_NAME
        jdk = "/Volumes/hendraa-laptop2/Windows JDK/jdk1.8.0_202"
    }
    verbose = true
    openOnDone = true
}