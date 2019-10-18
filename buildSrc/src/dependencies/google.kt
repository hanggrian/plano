const val VERSION_TRUTH = "1.0"
const val VERSION_GSON = "2.8.5"

fun Dependencies.google(repo: String? = null, module: String, version: String) =
    "com.google${repo?.let { ".$it" }.orEmpty()}:$module:$version"