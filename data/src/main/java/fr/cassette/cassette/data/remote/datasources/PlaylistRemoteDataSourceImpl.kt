package fr.cassette.cassette.data.remote.datasources

import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.dto.PlaylistListResponseDto
import fr.cassette.cassette.domain.models.PlaylistList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class PlaylistRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun getAllPlaylists(): List<PlaylistList> {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getPlaylists.view") {
                    parameter("f", "json")
                }.body<PlaylistListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getPlaylists failed")
        }

        return subsonicResponse.playlists
            ?.playlist
            .orEmpty()
            .map { it.toListDomain() }
    }
}
