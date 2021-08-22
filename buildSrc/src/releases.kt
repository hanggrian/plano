const val SDK_MIN = 14
const val SDK_TARGET = 30

const val RELEASE_GROUP = "com.hendraanggrian"
const val RELEASE_ARTIFACT = "plano"
const val RELEASE_VERSION = "0.1"
const val RELEASE_DESCRIPTION = "Multi-platform efficient paper size calculator app"
const val RELEASE_GITHUB = "https://github.com/hendraanggrian/$RELEASE_ARTIFACT"
const val RELEASE_DEBUG = true

fun getGithubRemoteUrl(artifact: String = RELEASE_ARTIFACT) =
    `java.net`.URL("$RELEASE_GITHUB/tree/main/$artifact/src")