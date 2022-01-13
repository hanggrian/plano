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
    sdk = "/Users/hendraanggrian/Library/JavaFX/javafx-sdk-17.0.2"
    modules("javafx.controls", "javafx.swing")
}

application {
    applicationName = "Plano"
    mainClass.set("$RELEASE_GROUP.plano.PlanoApp")
    applicationDefaultJvmArgs = listOf(
        "--module-path=/Users/hendraanggrian/Library/JavaFX/javafx-sdk-17.0.2/lib",
        "--add-modules=javafx.controls,javafx.swing",
        "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-opens=javafx.controls/javafx.scene.control.skin=com.jfoenix",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=com.jfoenix"
    )
}

packaging {
    modulePaths.add(File("/Users/hendraanggrian/Library/JavaFX/javafx-jmods-17.0.2"))
    modules.addAll("javafx.controls", "javafx.swing", "java.sql")
    javaArgs.addAll(
        "--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-opens=javafx.controls/javafx.scene.control.skin=com.jfoenix",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=com.jfoenix"
    )
    verbose.set(true)
    mac {
        icon.set(rootDir.resolve("arts/logo_mac.icns"))
    }
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
        enableCss()
        properties {
            writeResourceBundle.set(true)
        }
    }
    generateBuildConfig {
        packageName.set("$RELEASE_GROUP.$RELEASE_ARTIFACT")
        appName.set("Plano")
        debug.set(RELEASE_DEBUG)
        addField("USER", "hendraanggrian")
        addField("WEB", RELEASE_GITHUB)
    }
}