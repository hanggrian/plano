group = RELEASE_GROUP
version = RELEASE_VERSION

plugins {
    java
    javafx
    application
    kotlin("jvm")
    kotlin("kapt")
    hendraanggrian("generating")
    hendraanggrian("packaging")
}

javafx {
    sdk = System.getenv("JAVAFX_HOME")
    modules("javafx.controls", "javafx.swing")
}

application {
    mainClass.set("$RELEASE_GROUP.plano.PlanoApp")
    applicationDefaultJvmArgs = listOf(
        "--module-path=${System.getenv("JAVAFX_HOME")}/lib",
        "--add-modules=javafx.controls,javafx.swing"
    )
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("res")
    }
}

ktlint()

dependencies {
    api(project(":$RELEASE_ARTIFACT"))
    api(kotlin("reflect", VERSION_KOTLIN))
    api(kotlinx("coroutines-javafx", VERSION_COROUTINES))
    api(exposed("core"))
    api(exposed("dao"))
    api(exposed("jdbc"))
    api(sqliteJDBC())
    implementation(hendraanggrian("ktfx", "ktfx", VERSION_KTFX))
    implementation(hendraanggrian("ktfx", "jfoenix", VERSION_KTFX))
    implementation(hendraanggrian("auto", "prefs-jvm", VERSION_PREFS))
    kapt(hendraanggrian("auto", "prefs-compiler", VERSION_PREFS))
}

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.WARN // Gradle 7 bug
    }

    generateR {
        packageName.set("$RELEASE_GROUP.$RELEASE_ARTIFACT")
        resourcesDirectory.set(projectDir.resolve("res"))
        configureCss()
        properties { isWriteResourceBundle = true }
    }
    generateBuildConfig {
        packageName.set("$RELEASE_GROUP.$RELEASE_ARTIFACT")
        appName.set("Plano")
        artifactId.set(RELEASE_ARTIFACT)
        debug.set(RELEASE_DEBUG)
        addField("USER", "hendraanggrian")
        addField("WEB", RELEASE_GITHUB)
    }

    packMacOS {
        appName.set("$RELEASE_ARTIFACT-$RELEASE_VERSION/Plano.app")
        icon.set(rootProject.projectDir.resolve("art/$RELEASE_ARTIFACT.icns"))
        bundleId.set(RELEASE_GROUP)
    }
    packWindows32 {
        appName.set("$RELEASE_ARTIFACT-$RELEASE_VERSION-x86/Plano")
        jdk.set("/Volumes/Media/Windows JDK/jdk1.8.0_271-x86")
    }
    packWindows64 {
        appName.set("$RELEASE_ARTIFACT-$RELEASE_VERSION-x64/Plano")
        jdk.set("/Volumes/Media/Windows JDK/jdk1.8.0_271-x64")
    }
    withType<com.hendraanggrian.packaging.PackTask> {
        dependsOn("installDist")
    }
}

packaging {
    minimizeJre.set("hard")
    verbose.set(true)
    autoOpen.set(true)
}