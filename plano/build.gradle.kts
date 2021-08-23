import java.util.Locale

group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    `java-library`
    kotlin("jvm")
    hendraanggrian("localization")
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("res")
    }
    test {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

ktlint()

dependencies {
    api(kotlin("stdlib", VERSION_KOTLIN))
    api(kotlinx("coroutines-core", VERSION_COROUTINES))
    api(ktor("client-okhttp"))
    api(ktor("client-gson"))
    api(apache("maven-artifact", VERSION_MAVEN))
    api(apache("commons-math3", VERSION_COMMONS_MATH))
    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(google("truth", "truth", VERSION_TRUTH))
}

tasks {
    localizeJvm {
        resourceName.set("string")
        outputDirectory.set(projectDir.resolve("../$RELEASE_ARTIFACT-javafx/res"))
    }
    localizeAndroid {
        defaultLocale.set(Locale.ENGLISH)
        outputDirectory.set(projectDir.resolve("../$RELEASE_ARTIFACT-android/res"))
    }
}

localization {
    importCSV(projectDir.resolve("locale.csv"))
}