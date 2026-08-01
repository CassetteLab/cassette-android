package fr.cassettelabs.cassette.data.remote.datasources

import fr.cassettelabs.cassette.core.coroutines.CoroutineDispatchers
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.dto.AlbumListResponseDto
import fr.cassettelabs.cassette.data.remote.ktor.sha256
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.StarredLibrary
import fr.cassettelabs.cassette.domain.models.Track
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.withContext

internal class AlbumRemoteDataSourceImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
    private val httpClient: HttpClient,
    private val coverArtProcessor: CoverArtProcessor,
    private val coroutineDispatchers: CoroutineDispatchers,
) {
    suspend fun getRecentlyAddedAlbums(size: Int): List<Album> = getAlbumList(type = "newest", size = size)

    suspend fun getAllAlbums(size: Int): List<Album> = getAlbumList(type = "alphabeticalByName", size = size)

    suspend fun getStarredLibrary(): StarredLibrary {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getStarred2.view") {
                    parameter("f", "json")
                }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getStarred2 failed")
        }

        val starred = subsonicResponse.starred2
        return StarredLibrary(
            albums = starred?.album.orEmpty().map { it.toDomain() },
            tracks = starred?.song.orEmpty().map { it.toDomain() },
        )
    }

    private suspend fun getAlbumList(
        type: String,
        size: Int,
    ): List<Album> {
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
            .map { it.toDomain() }
    }

    suspend fun getAlbum(albumId: String): Album {
        return getAlbumDto(albumId).toDomain()
    }

    suspend fun getAlbumWithTracks(albumId: String): Pair<Album, List<Track>> {
        val album = getAlbumDto(albumId)
        return album.toDomain() to album.tracksToDomain()
    }

    suspend fun getAlbumTracks(albumId: String): List<Track> = getAlbumDto(albumId).tracksToDomain()

    suspend fun getArtistWithAlbums(artistId: String): Pair<Artist, List<Album>> {
        val artist = getArtistDto(artistId)
        return artist.toDomain() to artist.albumsToDomain()
    }

    private suspend fun getArtistDto(artistId: String) =
        getArtistResponse(artistId).subsonicResponse.artist
            ?: throw IllegalStateException("Subsonic getArtist returned no artist")

    private suspend fun getArtistResponse(artistId: String): AlbumListResponseDto {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val response =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getArtist.view") {
                    parameter("id", artistId)
                    parameter("f", "json")
                }.body<AlbumListResponseDto>()

        val subsonicResponse = response.subsonicResponse
        if (subsonicResponse.status != "ok") {
            throw IllegalStateException("Subsonic getArtist failed")
        }

        return response
    }

    private suspend fun getAlbumDto(albumId: String) =
        getAlbumResponse(albumId).subsonicResponse.album
            ?: throw IllegalStateException("Subsonic getAlbum returned no album")

    private suspend fun getAlbumResponse(albumId: String): AlbumListResponseDto {
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

        return response
    }

    suspend fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
    ): AlbumCoverArt = withContext(coroutineDispatchers.io) {
        val configuration =
            serverConfigurationDao.getServerConfiguration()
                ?: throw IllegalStateException("No server configuration found")
        val server = configuration.serverConfiguration

        val cacheKey = coverArtCacheKey(server.serverUrl, coverArtId, size)

        val bytes =
            httpClient
                .get("${server.serverUrl.trimEnd('/')}/rest/getCoverArt.view") {
                    parameter("id", coverArtId)
                    size?.let { parameter("size", it) }
                }.body<ByteArray>()

        val filePath = coverArtProcessor.saveCoverArt(bytes, cacheKey)
        AlbumCoverArt(filePath = filePath)
    }

    private fun coverArtCacheKey(
        serverUrl: String,
        coverArtId: String,
        size: Int?,
    ): String = sha256("${serverUrl.trimEnd('/')}|$coverArtId|${size.orEmpty()}")

    private fun Int?.orEmpty(): String = this?.toString().orEmpty()
}
