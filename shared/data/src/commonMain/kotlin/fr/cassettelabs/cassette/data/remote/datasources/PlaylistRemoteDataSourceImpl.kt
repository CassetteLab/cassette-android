package fr.cassettelabs.cassette.data.remote.datasources

import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.dto.PlaylistListResponseDto
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class PlaylistRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun getAllPlaylists(): List<Playlist> {
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
            .map { it.toDomain() }
    }

    suspend fun getPlaylist(playlistId: String): Playlist = getPlaylistDto(playlistId).toDomain()

    suspend fun getPlaylistWithTracks(playlistId: String): Pair<Playlist, List<Track>> {
        val playlist = getPlaylistDto(playlistId)
        return playlist.toDomain() to playlist.tracksToDomain()
    }

    private suspend fun getPlaylistDto(playlistId: String) =
        getPlaylistResponse(playlistId).subsonicResponse.playlist
            ?: throw IllegalStateException("Subsonic getPlaylist returned no playlist")

    suspend fun createPlaylist(
        name: String,
        songIds: List<String> = emptyList(),
    ): Playlist {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/createPlaylist.view") {
                    parameter("name", name)
                    songIds.forEach { songId -> parameter("songId", songId) }
                    parameter("f", "json")
                }.body<PlaylistListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic createPlaylist failed")
        }

        return subsonicResponse.playlist?.toDomain()
            ?: throw IllegalStateException("Subsonic createPlaylist returned no playlist")
    }

    private suspend fun getPlaylistResponse(playlistId: String): PlaylistListResponseDto {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getPlaylist.view") {
                    parameter("id", playlistId)
                    parameter("f", "json")
                }.body<PlaylistListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getPlaylist failed")
        }

        return response
    }
}
