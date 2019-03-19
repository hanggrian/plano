import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.ktor(module: String) = "io.ktor:ktor-$module:1.1.3"

fun DependencyHandler.kotlinx(
    module: String,
    version: String? = null
) = "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" } ?: ""}"

fun DependencyHandler.android() = "com.android.tools.build:gradle:3.5.0-alpha07"
fun PluginDependenciesSpec.android(submodule: String) = id("com.android.$submodule")

fun DependencyHandler.androidx(
    repository: String,
    module: String = repository,
    version: String = VERSION_ANDROIDX
): String = "androidx.$repository:$module:$version"

fun DependencyHandler.material(version: String = VERSION_ANDROIDX) =
    "com.google.android.material:material:$version"

fun DependencyHandler.hendraanggrian(module: String, version: String) =
    "com.hendraanggrian:$module:$version"

fun DependencyHandler.hendraanggrian(
    repository: String,
    module: String,
    version: String
) = "com.hendraanggrian.$repository:$module:$version"

fun DependencyHandler.apache(module: String, version: String) =
    "org.apache.${module.split("-")[0]}:$module:$version"

fun DependencyHandler.truth() = "com.google.truth:truth:0.43"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:4.0.1"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:0.3.3"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

fun DependencyHandler.ktlint(module: String? = null) = when (module) {
    null -> "com.github.shyiko:ktlint:0.31.0"
    else -> "com.github.shyiko.ktlint:ktlint-$module:0.31.0"
}

private fun String?.orEmpty(wrapper: (String) -> String) = this?.let(wrapper).orEmpty()
