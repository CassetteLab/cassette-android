package fr.cassettelabs.cassette.data.remote.datasources

import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.dto.AlbumListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class TrackRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun setTrackStarred(
        trackId: String,
        isStarred: Boolean,
    ) {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration
        val endpoint = if (isStarred) "star" else "unstar"

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/$endpoint.view") {
                    parameter("id", trackId)
                    parameter("f", "json")
                }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic $endpoint failed")
        }
    }
}
