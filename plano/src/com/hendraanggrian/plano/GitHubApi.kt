package com.hendraanggrian.plano

import com.hendraanggrian.plano.data.Release
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom

object GitHubApi {
    private const val endPoint = "https://api.github.com"
    private val client: HttpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun getLatestRelease(): Release = client.get {
        apiUrl("repos/hendraanggrian/plano/releases/latest")
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}
