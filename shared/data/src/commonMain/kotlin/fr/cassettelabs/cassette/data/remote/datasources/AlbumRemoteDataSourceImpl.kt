package fr.cassettelabs.cassette.data.remote.datasources

import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.dto.AlbumListResponseDto
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.domain.models.Track
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class AlbumRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> = getAlbumList(type = "newest", size = size)

    suspend fun getAllAlbums(size: Int): List<AlbumList> = getAlbumList(type = "alphabeticalByName", size = size)

    private suspend fun getAlbumList(
        type: String,
        size: Int,
    ): List<AlbumList> {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getAlbumList2.view") {
                    parameter("type", type)
                    parameter("size", size)
                    parameter("f", "json")
                }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getAlbumList2 failed")
        }

        return subsonicResponse.albumList2
            ?.album
            .orEmpty()
            .map { it.toListDomain() }
    }

    suspend fun getAlbum(albumId: String): AlbumDetail {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getAlbum.view") {
                    parameter("id", albumId)
                    parameter("f", "json")
                }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getAlbum failed")
        }

        return subsonicResponse.album?.toDetailDomain()
            ?: throw IllegalStateException("Subsonic getAlbum returned no album")
    }

    suspend fun getAlbumTracks(albumId: String): List<Track> = getAlbum(albumId).tracks

    suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
    ): AlbumCoverArt {
        throw UnsupportedOperationException("Album cover art file caching is not migrated to commonMain yet")
    }
}
