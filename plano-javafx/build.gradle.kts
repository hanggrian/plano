group = RELEASE_GROUP
version = RELEASE_VERSION_JAVAFX

plugins {
    kotlin("jvm")
    kotlin("kapt")
    idea
    hendraanggrian("r")
    hendraanggrian("buildconfig")
    hendraanggrian("packr")
    shadow
    application
}

application.mainClassName = "$RELEASE_GROUP.App"

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
}

val configuration = configurations.register("ktlint")

dependencies {
    api(project(":$RELEASE_ARTIFACT"))
    api(kotlin("stdlib", VERSION_KOTLIN))
    api(kotlinx("coroutines-javafx", VERSION_COROUTINES))

    implementation(apache("commons-lang3", VERSION_COMMONS_LANG))
    implementation(hendraanggrian("ktfx", "ktfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "ktfx-jfoenix", VERSION_KTFX))

    implementation(hendraanggrian("prefs", "prefs-jvm", VERSION_PREFS))
    kapt(hendraanggrian("prefs", "prefs-compiler", VERSION_PREFS))

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
        main = "com.pinterest.ktlint.Main"
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
        main = "com.pinterest.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    withType<com.hendraanggrian.r.RTask> {
        resourcesDirectory = "res"
        useProperties {
            readResourceBundle = true
        }
    }

    withType<com.hendraanggrian.buildconfig.BuildConfigTask> {
        appName = RELEASE_NAME
        artifactId = RELEASE_ARTIFACT
        debug = RELEASE_DEBUG
        field("USER", RELEASE_USER)
        field("HOMEPAGE", RELEASE_HOMEPAGE)
    }

    named<Jar>("jar") {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDirectory.set(buildDir.resolve("releases"))
        archiveBaseName.set(RELEASE_ARTIFACT)
        archiveVersion.set(RELEASE_VERSION_JAVAFX)
        archiveClassifier.set(null as String?)
    }

    withType<com.hendraanggrian.packr.PackTask> {
        dependsOn("installDist")
    }
}

packr {
    mainClass = application.mainClassName
    executable = RELEASE_NAME
    classpath = files("build/install/$RELEASE_ARTIFACT-javafx/lib")
    resources = files("res")
    minimizeJre = "hard"
    macOS {
        name = "$RELEASE_NAME/$RELEASE_NAME.app"
        icon = rootProject.projectDir.resolve("art/$RELEASE_ARTIFACT.icns")
        bundleId = RELEASE_GROUP
    }
    windows32 {
        name = "32-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_231-x86"
    }
    windows64 {
        name = "64-bit/$RELEASE_NAME"
        jdk = "/Volumes/Media/Windows JDK/jdk1.8.0_231-x64"
    }
    verbose = true
    openOnDone = true
}