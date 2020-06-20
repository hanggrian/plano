import java.util.Locale

group = RELEASE_GROUP

plugins {
    `java-library`
    kotlin("jvm")
    hendraanggrian("locale")
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
    api(apache("commons-math3", VERSION_COMMONS_MATH))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(google("truth", "truth", VERSION_TRUTH))

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

    withType<com.hendraanggrian.locale.LocalizeJavaTask> {
        resourceName = "string"
        outputDirectory = "../$RELEASE_ARTIFACT-javafx/res"
    }
    withType<com.hendraanggrian.locale.LocalizeAndroidTask> {
        defaultLocale = Locale.ENGLISH
        outputDirectory = "../$RELEASE_ARTIFACT-android/res"
    }
}

locale {
    importCSV("locale.csv")
}