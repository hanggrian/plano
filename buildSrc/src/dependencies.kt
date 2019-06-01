import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.ktor(module: String) = "io.ktor:ktor-$module:1.1.4"

fun DependencyHandler.kotlinx(
    module: String,
    version: String? = null
) = "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" } ?: ""}"

fun DependencyHandler.android() = "com.android.tools.build:gradle:3.5.0-beta03"
fun PluginDependenciesSpec.android(submodule: String) = id("com.android.$submodule")

fun DependencyHandler.androidx(
    repository: String,
    module: String = repository,
    version: String = VERSION_ANDROIDX
): String = "androidx.$repository:$module:$version"

fun DependencyHandler.material(version: String = VERSION_ANDROIDX) =
    "com.google.android.material:material:$version"

fun DependencyHandler.leakCanary(module: String? = null) =
    "com.squareup.leakcanary:leakcanary-android${module.orEmpty { "-$it" }}:2.0-alpha-1"

fun DependencyHandler.hendraanggrian(module: String, version: String) =
    "com.hendraanggrian:$module:$version"

fun DependencyHandler.hendraanggrian(
    repository: String,
    module: String,
    version: String
) = "com.hendraanggrian.$repository:$module:$version"

fun PluginDependenciesSpec.hendraanggrian(module: String) = id("com.hendraanggrian.$module")

fun DependencyHandler.apache(module: String, version: String) =
    "org.apache.${module.split("-")[0]}:$module:$version"

fun DependencyHandler.truth() = "com.google.truth:truth:0.44"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:4.0.1"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:0.3.3"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

fun DependencyHandler.ktlint(module: String? = null) = when (module) {
    null -> "com.pinterest:ktlint:0.32.0"
    else -> "com.pinterest:ktlint-$module:0.32.0"
}

private fun String?.orEmpty(wrapper: (String) -> String) = this?.let(wrapper).orEmpty()
