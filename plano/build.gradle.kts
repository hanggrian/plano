group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    `java-library`
    kotlin("jvm")
    dokka()
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.buildconfig")
}

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    get("test").java.srcDir("tests/src")
}

val configuration = configurations.register("ktlint")

dependencies {
    api(kotlin("stdlib", VERSION_KOTLIN))

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(truth())

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
    register<JavaExec>("ktlintFormat") {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configuration.get())
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    named<com.hendraanggrian.r.RTask>("generateR") {
        className = "R2"
        resourcesDirectory = "res"
        useProperties {
            readResourceBundle = true
        }
    }

    named<com.hendraanggrian.buildconfig.BuildConfigTask>("generateBuildConfig") {
        appName = RELEASE_NAME
        artifactId = RELEASE_ARTIFACT
        debug = RELEASE_DEBUG
        website = RELEASE_WEBSITE
    }

    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}
