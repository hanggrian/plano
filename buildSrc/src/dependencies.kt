internal typealias Dependencies = org.gradle.api.artifacts.dsl.DependencyHandler
internal typealias Plugins = org.gradle.plugin.use.PluginDependenciesSpec

const val SDK_MIN = 14
const val SDK_TARGET = 31
const val VERSION_MULTIDEX = "2.0.1"
const val VERSION_LIFECYCLE = "2.2.0"
const val VERSION_ROOM = "2.4.0"
const val VERSION_ANDROIDX = "1.3.0"
const val VERSION_ANDROIDX_TEST = "1.4.0"
const val VERSION_ANDROIDX_JUNIT = "1.1.3"
const val VERSION_ANDROIDX_TRUTH = "1.4.0"
const val VERSION_ESPRESSO = "3.4.0"
val Dependencies.android get() = "com.android.tools.build:gradle:7.0.4"
fun Plugins.android(submodule: String) = id("com.android.$submodule")
fun Dependencies.material(version: String = VERSION_ANDROIDX) = "com.google.android.material:material:$version"
fun Dependencies.androidx(repository: String, module: String = repository, version: String = VERSION_ANDROIDX) =
    "androidx.$repository:$module:$version"

const val VERSION_JAVAFX_PLUGIN = "0.0.10"
val Dependencies.javafx get() = "org.openjfx:javafx-plugin:$VERSION_JAVAFX_PLUGIN"
val Plugins.javafx get() = id("org.openjfx.javafxplugin")

const val VERSION_KOTLIN = "1.6.10"
const val VERSION_DOKKA = "1.6.0"
const val VERSION_COROUTINES = "1.6.0"
const val VERSION_EXPOSED = "0.37.2"
val Dependencies.dokka get() = "org.jetbrains.dokka:dokka-gradle-plugin:$VERSION_DOKKA"
val Plugins.dokka get() = id("org.jetbrains.dokka")
fun Dependencies.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"
fun Dependencies.exposed(module: String) = "org.jetbrains.exposed:exposed-$module:$VERSION_EXPOSED"

val Dependencies.`git-publish` get() = "org.ajoberstar:gradle-git-publish:3.0.0"
val Plugins.`git-publish` get() = id("org.ajoberstar.git-publish")

const val VERSION_MAVEN = "3.8.3"
const val VERSION_COMMONS_MATH = "3.6.1"
fun Dependencies.apache(module: String, version: String): String {
    require('-' in module) { "Module must contain `-` (e.g.: commons-lang, commons-math)." }
    return "org.apache.${module.split('-').first()}:$module:$version"
}

const val VERSION_TRUTH = "1.1.3"
fun Dependencies.google(repo: String? = null, module: String, version: String) =
    "com.google${repo?.let { ".$it" }.orEmpty()}:$module:$version"

const val VERSION_KTFX = "0.1-SNAPSHOT"
const val VERSION_PREFS = "0.1-SNAPSHOT"
const val VERSION_BUNDLES = "0.1-SNAPSHOT"
const val VERSION_PLUGIN_GENERATING = "0.1"
const val VERSION_PLUGIN_LOCALIZATION = "0.1"
const val VERSION_PLUGIN_PACKAGING = "0.1"
fun Dependencies.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"
fun Dependencies.hendraanggrian(repo: String, module: String, version: String) =
    "com.hendraanggrian.$repo:$module:$version"
fun Plugins.hendraanggrian(module: String) = id("com.hendraanggrian.$module")

fun Dependencies.leakCanary() = "com.squareup.leakcanary:leakcanary-android:2.7"

fun Dependencies.processPhoenix() = "com.jakewharton:process-phoenix:2.1.2"

fun Dependencies.sqliteJDBC() = "org.xerial:sqlite-jdbc:3.36.0.3"

fun Dependencies.ktor(module: String) = "io.ktor:ktor-$module:1.6.7"
