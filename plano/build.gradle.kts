group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    `java-library`
    kotlin("jvm")
    idea
    id("com.hendraanggrian.r")
    id("com.hendraanggrian.buildconfig")
    id("com.hendraanggrian.locale")
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
    api(kotlinx("coroutines-core", VERSION_COROUTINES))
    api(ktor("client-okhttp"))
    api(ktor("client-gson"))
    api(apache("maven-artifact", VERSION_MAVEN))

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(truth())

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
        field("USER", RELEASE_USER)
        field("HOMEPAGE", RELEASE_HOMEPAGE)
    }
}

locale {

}
