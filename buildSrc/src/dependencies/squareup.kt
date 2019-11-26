const val VERSION_LEAKCANARY = "2.0-beta-3"

fun Dependencies.squareup(repo: String, module: String = repo, version: String) = "com.squareup.$repo:$module:$version"