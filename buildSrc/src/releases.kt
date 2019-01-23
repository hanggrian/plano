const val RELEASE_USER = "hendraanggrian"
const val RELEASE_ARTIFACT = "plano"
const val RELEASE_GROUP = "com.$RELEASE_USER.$RELEASE_ARTIFACT"
const val RELEASE_VERSION = "1.0"
const val RELEASE_DESC = "Plano paper"
const val RELEASE_WEBSITE = "https://github.com/$RELEASE_USER/$RELEASE_ARTIFACT"
const val RELEASE_DEBUG = true

val BINTRAY_USER = System.getenv("BINTRAY_USER")
val BINTRAY_KEY = System.getenv("BINTRAY_KEY")