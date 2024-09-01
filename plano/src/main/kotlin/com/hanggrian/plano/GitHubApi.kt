package com.hanggrian.plano

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import org.apache.maven.artifact.versioning.ComparableVersion

public object GitHubApi {
    private const val ENDPOINT = "https://api.github.com"
    private val client: HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                gson()
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.CacheControl, "no-cache")
            }
        }

    public suspend fun getRelease(extension: String): Release =
        client
            .get("$ENDPOINT/repos/hanggrian/plano/releases")
            .body<List<Release>>()
            .firstOrNull { release -> release.assets.any { it.name.endsWith(extension) } }
            ?: Release.NOT_FOUND

    public data class Asset(
        @SerializedName("browser_download_url") val downloadUrl: String,
        val name: String,
        val state: String,
    ) {
        public fun isUploaded(): Boolean = state == "uploaded"

        override fun toString(): String = name
    }

    public data class Release(
        @SerializedName("html_url") val htmlUrl: String,
        val name: String,
        val assets: List<Asset>,
    ) {
        public fun isNewerThan(currentVersion: String): Boolean =
            ComparableVersion(name) >
                ComparableVersion(
                    currentVersion,
                ) &&
                assets.isNotEmpty() &&
                assets.all { it.isUploaded() }

        internal companion object {
            val NOT_FOUND = Release("", "", emptyList())
        }
    }
}
