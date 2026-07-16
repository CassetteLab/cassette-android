package fr.cassette.cassette.data.remote.datasources

import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.dto.AlbumListResponseDto
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class AlbumRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response = httpClient.get("${server.serverUrl.trimEnd('/')}/rest/getAlbumList2.view") {
            parameter("type", "newest")
            parameter("size", size)
        }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getAlbumList2 failed")
        }

        return subsonicResponse.albumList2?.album.orEmpty().map { it.toListDomain() }
    }

    suspend fun getAlbum(albumId: String): AlbumDetail {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response = httpClient.get("${server.serverUrl.trimEnd('/')}/rest/getAlbum.view") {
            parameter("id", albumId)
        }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getAlbum failed")
        }

        return subsonicResponse.album?.toDetailDomain()
            ?: throw IllegalStateException("Subsonic getAlbum returned no album")
    }
}
