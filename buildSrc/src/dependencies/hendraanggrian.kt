const val VERSION_KTFX = "8.7.3"
const val VERSION_PREFS = "0.3"
const val VERSION_BUNDLER = "0.3-rc1"

fun Dependencies.hendraanggrian(module: String, version: String) = "com.hendraanggrian:$module:$version"

fun Dependencies.hendraanggrian(repo: String, module: String, version: String) =
    "com.hendraanggrian.$repo:$module:$version"

fun Plugins.hendraanggrian(module: String) = id("com.hendraanggrian.$module")
