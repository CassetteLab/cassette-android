package fr.cassette.cassette.data.remote.datasources

import android.content.Context
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.dto.AlbumListResponseDto
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.domain.models.Track
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.io.File
import java.security.MessageDigest

internal class AlbumRemoteDataSourceImpl(
    private val context: Context,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
) {
    suspend fun getRecentlyAddedAlbums(size: Int): List<AlbumList> {
        return getAlbumList(type = "newest", size = size)
    }

    suspend fun getAllAlbums(size: Int): List<AlbumList> {
        return getAlbumList(type = "alphabeticalByName", size = size)
    }

    private suspend fun getAlbumList(type: String, size: Int): List<AlbumList> {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response = httpClient.get("${server.serverUrl.trimEnd('/')}/rest/getAlbumList2.view") {
            parameter("type", type)
            parameter("size", size)
            parameter("f", "json")
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
            parameter("f", "json")
        }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getAlbum failed")
        }

        return subsonicResponse.album?.toDetailDomain()
            ?: throw IllegalStateException("Subsonic getAlbum returned no album")
    }

    suspend fun getAlbumTracks(albumId: String): List<Track> {
        return getAlbum(albumId).tracks
    }

    suspend fun getAlbumCoverArt(coverArtId: String, size: Int?): AlbumCoverArt {
        val configuration = serverConfigurationDao.getServerConfiguration()
            ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val cacheFile = File(coverArtCacheDirectory(), "${coverArtCacheKey(server.serverUrl, coverArtId, size)}.img")
        if (cacheFile.exists() && cacheFile.length() > 0L) {
            return AlbumCoverArt(filePath = cacheFile.absolutePath)
        }

        val bytes = httpClient.get("${server.serverUrl.trimEnd('/')}/rest/getCoverArt.view") {
            parameter("id", coverArtId)
            size?.let { parameter("size", it) }
        }.body<ByteArray>()

        val temporaryFile = File(cacheFile.parentFile, "${cacheFile.name}.tmp")
        temporaryFile.writeBytes(bytes)
        if (!temporaryFile.renameTo(cacheFile)) {
            temporaryFile.copyTo(cacheFile, overwrite = true)
            temporaryFile.delete()
        }

        return AlbumCoverArt(filePath = cacheFile.absolutePath)
    }

    private fun coverArtCacheDirectory(): File {
        return File(context.cacheDir, COVER_ART_CACHE_DIRECTORY).apply { mkdirs() }
    }

    private fun coverArtCacheKey(serverUrl: String, coverArtId: String, size: Int?): String {
        return sha256("${serverUrl.trimEnd('/')}|$coverArtId|${size.orEmpty()}")
    }

    private fun Int?.orEmpty(): String = this?.toString().orEmpty()

    private fun sha256(value: String): String = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray())
        .joinToString(separator = "") { byte -> "%02x".format(byte) }

    private companion object {
        const val COVER_ART_CACHE_DIRECTORY = "cover_art"
    }
}
