package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.PlaylistDao
import fr.cassettelabs.cassette.data.local.dao.PlaylistTrackDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.dao.AlbumTrackDao
import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.CoverArtDao
import fr.cassettelabs.cassette.data.local.embeddeds.PlaylistWithCoverArt
import fr.cassettelabs.cassette.data.local.embeddeds.TrackWithAlbumAndCoverArt
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.AlbumTrackEntity
import fr.cassettelabs.cassette.data.local.entities.CoverArtEntity
import fr.cassettelabs.cassette.data.local.entities.PlaylistEntity
import fr.cassettelabs.cassette.data.local.entities.PlaylistTrackEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.datasources.PlaylistRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class PlaylistRepositoryImpl(
    private val playlistRemoteDataSource: PlaylistRemoteDataSourceImpl,
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val albumTrackDao: AlbumTrackDao,
    private val coverArtDao: CoverArtDao,
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists(COVER_ART_SIZE_KEY).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getPlaylist(playlistId: String): Playlist {
        val localPlaylist = playlistDao.getPlaylistWithCoverArt(playlistId, COVER_ART_SIZE_KEY)
        val playlist =
            playlistRemoteDataSource
                .getPlaylist(playlistId)
                .copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
        playlistDao.insertPlaylist(playlist.toEntity(localPlaylist?.serverConfigurationId ?: currentServerConfigurationId()))
        return playlistDao.getPlaylistWithCoverArt(playlistId, COVER_ART_SIZE_KEY)?.toDomain()
            ?: throw IllegalStateException("Playlist $playlistId was not stored")
    }

    override suspend fun getPlaylistTracks(playlistId: String): List<Track> {
        val localPlaylist = playlistDao.getPlaylistWithCoverArt(playlistId, COVER_ART_SIZE_KEY)
        try {
            val (remotePlaylist, remoteTracks) = playlistRemoteDataSource.getPlaylistWithTracks(playlistId)
            val playlistWithLocalData =
                remotePlaylist.copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
            playlistDao.insertPlaylist(playlistWithLocalData.toEntity(localPlaylist?.serverConfigurationId ?: currentServerConfigurationId()))
            insertTrackAlbums(remoteTracks)
            trackDao.insertTracks(remoteTracks.map { track -> track.toEntity() })
            albumTrackDao.insertAlbumTracks(remoteTracks.mapNotNull { track -> track.toAlbumTrackEntityOrNull() })
            playlistTrackDao.replacePlaylistTracks(
                playlistId = playlistId,
                tracks = remoteTracks.mapIndexed { index, track -> track.toPlaylistTrackEntity(playlistId, index) },
            )
        } catch (exception: Exception) {
            val localTracks = playlistTrackDao.getPlaylistTracks(playlistId, COVER_ART_SIZE_KEY)
            if (localTracks.isEmpty() && localPlaylist?.trackCount != 0) throw exception
        }

        return playlistTrackDao.getPlaylistTracks(playlistId, COVER_ART_SIZE_KEY).map { it.toDomain() }
    }

    override suspend fun refreshPlaylists() {
        val remotePlaylists = playlistRemoteDataSource.getAllPlaylists()
        val serverConfigurationId = currentServerConfigurationId()
        if (remotePlaylists.isEmpty()) {
            playlistDao.deleteAllPlaylists(serverConfigurationId)
            return
        }

        playlistDao.deletePlaylistsNotIn(
            serverConfigurationId = serverConfigurationId,
            playlistIds = remotePlaylists.map { it.id },
        )
        remotePlaylists.forEach { playlist ->
            val localPlaylist = playlistDao.getPlaylistWithCoverArt(playlist.id, COVER_ART_SIZE_KEY)
            val playlistWithLocalData =
                playlist.copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
            playlistDao.insertPlaylist(
                playlistWithLocalData.toEntity(localPlaylist?.serverConfigurationId ?: serverConfigurationId),
            )
        }
    }

    override suspend fun createPlaylist(name: String): Playlist {
        val playlist = playlistRemoteDataSource.createPlaylist(name)
        val serverConfigurationId = currentServerConfigurationId()
        playlistDao.insertPlaylist(playlist.toEntity(serverConfigurationId))
        return playlist
    }

    override fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int?,
        playlistId: String?,
    ): Flow<CoverArtLoadingStatus> = flow {
        val serverConfigurationId = currentServerConfigurationId()
        coverArtDao.getCoverArt(serverConfigurationId, coverArtId, size.toCoverArtSizeKey())?.validFilePath()?.let { filePath ->
            emit(CoverArtLoadingStatus.Loaded(filePath))
            return@flow
        }

        emit(CoverArtLoadingStatus.Loading)

        val coverArt = albumRemoteDataSource
            .getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                coverArtDao.insertCoverArt(
                    CoverArtEntity(
                        serverConfigurationId = serverConfigurationId,
                        coverArtId = coverArtId,
                        size = size.toCoverArtSizeKey(),
                        filePath = coverArt.filePath,
                        updatedAt = currentTimeMillis(),
                    ),
                )
                playlistId?.let { id ->
                    extractAndSaveSeedColor(id, coverArt.filePath)
                }
            }
        emit(CoverArtLoadingStatus.Loaded(coverArt.filePath))
    }.catch { throwable ->
        emit(CoverArtLoadingStatus.Error(throwable))
    }

    private suspend fun extractAndSaveSeedColor(
        playlistId: String,
        filePath: String,
    ) {
        val existingSeedColor = playlistDao.getPlaylist(playlistId)?.seedColor
        if (existingSeedColor != null) return

        try {
            coverArtProcessor.extractSeedColor(filePath)?.let { seedColor ->
                playlistDao.updateSeedColor(playlistId, seedColor)
            }
        } catch (_: Exception) {
        }
    }

    private fun Playlist.toEntity(existingServerConfigurationId: Long): PlaylistEntity =
        PlaylistEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId,
            name = name,
            trackCount = trackCount,
            coverArt = coverArt,
            created = created,
            seedColor = seedColor,
        )

    private fun PlaylistWithCoverArt.toDomain(): Playlist =
        Playlist(
            id = id,
            name = name,
            trackCount = trackCount,
            coverArt = coverArt,
            coverArtFilePath = validCoverArtFilePath(),
            created = created,
            seedColor = seedColor,
        )

    private fun Track.toPlaylistTrackEntity(
        playlistId: String,
        position: Int,
    ): PlaylistTrackEntity =
        PlaylistTrackEntity(
            playlistId = playlistId,
            trackId = id,
            position = position,
        )

    private fun Track.toEntity(): TrackEntity =
        TrackEntity(
            id = id,
            title = title,
            artist = artist,
            artistId = artistId,
            durationSeconds = durationSeconds,
            albumName = albumName,
            coverArt = coverArt,
            starredAt = starredAt,
        )

    private fun Track.toAlbumTrackEntityOrNull(): AlbumTrackEntity? {
        val albumId = albumId ?: return null
        return AlbumTrackEntity(
            albumId = albumId,
            trackId = id,
            trackNumber = trackNumber,
        )
    }

    private suspend fun insertTrackAlbums(tracks: List<Track>) {
        val serverConfigurationId = currentServerConfigurationId()
        tracks.forEach { track ->
            val albumId = track.albumId ?: return@forEach
            if (albumDao.getAlbum(albumId) != null) return@forEach

            albumDao.insertAlbum(
                AlbumEntity(
                    id = albumId,
                    serverConfigurationId = serverConfigurationId,
                    name = track.albumName ?: albumId,
                    artist = track.artist,
                    artistId = track.artistId,
                    coverArt = track.coverArt,
                ),
            )
        }
    }

    private fun TrackWithAlbumAndCoverArt.toDomain(): Track =
        Track(
            id = trackId,
            title = title,
            artist = artist,
            artistId = artistId,
            trackNumber = trackNumber,
            durationSeconds = durationSeconds,
            albumId = albumId,
            albumName = albumName,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            starredAt = starredAt,
        )

    private suspend fun currentServerConfigurationId(): Long =
        serverConfigurationDao.getServerConfiguration()?.serverConfiguration?.id
            ?: throw IllegalStateException("No server configuration found")

    private fun PlaylistWithCoverArt.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }

    private fun CoverArtEntity.validFilePath(): String? =
        filePath.takeIf { coverArtProcessor.fileExists(it) }

    private fun Int?.toCoverArtSizeKey(): Int = this ?: ORIGINAL_COVER_ART_SIZE_KEY

    private companion object {
        const val COVER_ART_SIZE_KEY = 900
        const val ORIGINAL_COVER_ART_SIZE_KEY = -1
    }
}
