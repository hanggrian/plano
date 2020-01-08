const val VERSION_LEAKCANARY = "2.1"

fun Dependencies.squareup(repo: String, module: String = repo, version: String) = "com.squareup.$repo:$module:$version"