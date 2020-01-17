package com.hendraanggrian.plano.data

import com.google.gson.annotations.SerializedName
import org.apache.maven.artifact.versioning.ComparableVersion

data class Release(
    @SerializedName("html_url") val htmlUrl: String,
    val name: String,
    val assets: List<Asset>
) {

    companion object {
        val NOT_FOUND = Release("", "", emptyList())
    }

    fun isNewerThan(currentVersion: String): Boolean =
        ComparableVersion(name) > ComparableVersion(currentVersion) &&
            assets.isNotEmpty() &&
            assets.all { it.isUploaded() }
}
