group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    application
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.generating.r")
    id("com.hendraanggrian.packr")
    id("com.github.johnrengelman.shadow")
}

application.mainClassName = "$RELEASE_GROUP.PlanoApplication"

sourceSets {
    getByName("main") {
        // manual import client generated build
        val dirs = mutableListOf("src")
        val clientGeneratedDir = "client/build/generated"
        if (rootDir.resolve(clientGeneratedDir).exists()) {
            dirs += "../$clientGeneratedDir/buildconfig/src/main"
            dirs += "../$clientGeneratedDir/r/src/main"
        }
        java.srcDirs(*dirs.toTypedArray())
        resources.srcDir("res")
    }
}

val configuration = configurations.register("ktlint")

dependencies {
    implementation(project(":$RELEASE_ARTIFACT"))
    implementation(kotlin("stdlib", VERSION_KOTLIN))

    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(jfoenix())
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
        configureProperties {
            readResourceBundle = true
        }
    }

    named<Jar>("jar") {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
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