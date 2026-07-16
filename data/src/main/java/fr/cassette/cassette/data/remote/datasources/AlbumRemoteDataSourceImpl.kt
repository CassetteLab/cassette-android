package fr.cassette.cassette.data.remote.datasources

import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.dto.AlbumListResponseDto
import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.domain.models.AlbumList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLBuilder
import java.security.MessageDigest

internal class AlbumRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
    private val cipherHelper: CipherHelper,
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

    suspend fun getAlbumCoverArtRequest(coverArtId: String, size: Int?): AlbumCoverArtRequest {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration
        val salt = System.currentTimeMillis().toString(16)
        val password = cipherHelper.decrypt(server.encryptedPassword)
        val url = URLBuilder("${server.serverUrl.trimEnd('/')}/rest/getCoverArt.view").apply {
            parameters.append("id", coverArtId)
            size?.let { parameters.append("size", it.toString()) }
            parameters.append("u", server.username)
            parameters.append("t", md5(password + salt))
            parameters.append("s", salt)
            parameters.append("v", "1.16.1")
            parameters.append("c", "Cassette")
        }.buildString()

        val headers = configuration.customHeaders
            .filter { it.name.isNotBlank() }
            .associate { customHeader ->
                customHeader.name to cipherHelper.decrypt(customHeader.encryptedValue)
            }

        return AlbumCoverArtRequest(url = url, headers = headers)
    }

    private fun md5(value: String): String = MessageDigest.getInstance("MD5")
        .digest(value.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
}
