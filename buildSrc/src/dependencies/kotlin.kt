const val VERSION_KOTLIN = "1.4.20"
const val VERSION_COROUTINES = "1.4.2"
const val VERSION_EXPOSED = "0.25.1"

fun Dependencies.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"

fun Dependencies.exposed(module: String) = "org.jetbrains.exposed:exposed-$module:$VERSION_EXPOSED"
