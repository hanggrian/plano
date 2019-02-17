import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.plugin.use.PluginDependenciesSpec

fun DependencyHandler.kotlinx(
    module: String,
    version: String? = null
) = "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" } ?: ""}"

fun DependencyHandler.dokka(module: String? = null) =
    "org.jetbrains.dokka:dokka-${module.orEmpty { "$it-" }}gradle-plugin:$VERSION_DOKKA"

fun PluginDependenciesSpec.dokka(module: String? = null) =
    id("org.jetbrains.dokka${module.orEmpty { "-$it" }}")

fun DependencyHandler.android() = "com.android.tools.build:gradle:$VERSION_ANDROID_PLUGIN"
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

fun DependencyHandler.truth() = "com.google.truth:truth:$VERSION_TRUTH"

fun DependencyHandler.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"

fun DependencyHandler.gitPublish() = "org.ajoberstar:gradle-git-publish:$VERSION_GIT_PUBLISH"
inline val PluginDependenciesSpec.`git-publish` get() = id("org.ajoberstar.git-publish")

fun DependencyHandler.ktlint(module: String? = null) = when (module) {
    null -> "com.github.shyiko:ktlint:$VERSION_KTLINT"
    else -> "com.github.shyiko.ktlint:ktlint-$module:$VERSION_KTLINT"
}

private fun String?.orEmpty(wrapper: (String) -> String) = this?.let(wrapper).orEmpty()
