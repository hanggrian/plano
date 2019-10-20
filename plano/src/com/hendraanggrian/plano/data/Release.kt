package com.hendraanggrian.plano.data

import org.apache.maven.artifact.versioning.ComparableVersion

data class Release(
    val name: String,
    val assets: List<Asset>
) {

    companion object {
        val NOT_FOUND = Release("", emptyList())
    }

    fun isNewerThan(currentVersion: String): Boolean =
        ComparableVersion(name) > ComparableVersion(currentVersion) &&
            assets.isNotEmpty() &&
            assets.all { it.isUploaded() }
}
