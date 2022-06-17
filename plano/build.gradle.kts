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
    implementation(kotlin("stdlib", VERSION_KOTLIN))
    implementation(kotlinx("coroutines-core", VERSION_COROUTINES))
    implementation(ktor("client-okhttp"))
    implementation(ktor("client-gson"))
    implementation(apache("maven-artifact", VERSION_MAVEN))
    implementation(apache("commons-math3", VERSION_COMMONS_MATH))
    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(google("truth", "truth", VERSION_TRUTH))
}

tasks {
    localizeJvm {
        resourceName.set("string")
        outputDirectory.set(rootDir.resolve("$RELEASE_ARTIFACT-javafx/res"))
    }
    localizeAndroid {
        outputDirectory.set(rootDir.resolve("$RELEASE_ARTIFACT-android/res"))
    }
}

localization {
    importCSV(projectDir.resolve("locale.csv"))
}
