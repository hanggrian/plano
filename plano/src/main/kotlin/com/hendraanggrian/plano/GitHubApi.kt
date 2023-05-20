package com.hendraanggrian.plano

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom
import org.apache.maven.artifact.versioning.ComparableVersion

object GitHubApi {
    private const val endpoint = "https://api.github.com"
    private val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun getRelease(extension: String): Release =
        client.get<List<Release>> { apiUrl("repos/hendraanggrian/plano/releases") }
            .firstOrNull { release -> release.assets.any { it.name.endsWith(extension) } }
            ?: Release.NOT_FOUND

    private fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endpoint)
            encodedPath = path
        }
    }

    data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        val name: String,
        val state: String
    ) {

        fun isUploaded(): Boolean = state == "uploaded"

        override fun toString(): String = name
    }

    data class Release(
        @SerializedName("html_url") val htmlUrl: String,
        val name: String,
        val assets: List<Asset>
    ) {

        companion object {
            val NOT_FOUND = Release("", "", emptyList())
        }

        fun isNewerThan(currentVersion: String): Boolean =
            ComparableVersion(name) > ComparableVersion(
                currentVersion
            ) &&
                assets.isNotEmpty() &&
                assets.all { it.isUploaded() }
    }
}
