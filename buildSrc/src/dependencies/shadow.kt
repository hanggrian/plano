private const val VERSION_SHADOW = "5.2.0"

fun Dependencies.shadow() = "com.github.jengelman.gradle.plugins:shadow:$VERSION_SHADOW"

val Plugins.shadow get() = id("com.github.johnrengelman.shadow")