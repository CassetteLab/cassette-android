package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.PlaylistDao
import fr.cassettelabs.cassette.data.local.dao.PlaylistTrackDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.entities.PlaylistEntity
import fr.cassettelabs.cassette.data.local.entities.PlaylistTrackEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.datasources.PlaylistRemoteDataSourceImpl
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
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val trackDao: TrackDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<Playlist>> =
        playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getPlaylist(playlistId: String): Playlist {
        val localPlaylist = playlistDao.getPlaylist(playlistId)
        val playlist =
            playlistRemoteDataSource
                .getPlaylist(playlistId)
                .copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
        playlistDao.insertPlaylist(playlist.toEntity(localPlaylist?.serverConfigurationId ?: currentServerConfigurationId()))
        return playlistDao.getPlaylist(playlistId)?.toDomain()
            ?: throw IllegalStateException("Playlist $playlistId was not stored")
    }

    override suspend fun getPlaylistTracks(playlistId: String): List<Track> {
        val localPlaylist = playlistDao.getPlaylist(playlistId)
        try {
            val (remotePlaylist, remoteTracks) = playlistRemoteDataSource.getPlaylistWithTracks(playlistId)
            val playlistWithLocalData =
                remotePlaylist.copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
            playlistDao.insertPlaylist(playlistWithLocalData.toEntity(localPlaylist?.serverConfigurationId ?: currentServerConfigurationId()))
            trackDao.insertTracks(remoteTracks.map { track -> track.toEntity() })
            playlistTrackDao.replacePlaylistTracks(
                playlistId = playlistId,
                tracks = remoteTracks.mapIndexed { index, track -> track.toPlaylistTrackEntity(playlistId, index) },
            )
        } catch (exception: Exception) {
            val localTracks = playlistTrackDao.getPlaylistTracks(playlistId)
            if (localTracks.isEmpty() && localPlaylist?.trackCount != 0) throw exception
        }

        return playlistTrackDao.getPlaylistTracks(playlistId).map { it.toDomain() }
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
            val localPlaylist = playlistDao.getPlaylist(playlist.id)
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

    override fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int?,
        playlistId: String?,
    ): Flow<CoverArtLoadingStatus> = flow {
        playlistId?.let { id ->
            playlistDao.getPlaylist(id)?.validCoverArtFilePath()?.let { filePath ->
                emit(CoverArtLoadingStatus.Loaded(filePath))
                return@flow
            }
        }

        emit(CoverArtLoadingStatus.Loading)

        val coverArt = albumRemoteDataSource
            .getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                playlistId?.let { id ->
                    playlistDao.updateCoverArtFilePath(playlistId = id, coverArtFilePath = coverArt.filePath)
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
            coverArtFilePath = coverArtFilePath,
            created = created,
            seedColor = seedColor,
        )

    private fun PlaylistEntity.toDomain(): Playlist =
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
            albumId = albumId,
            title = title,
            artist = artist,
            artistId = artistId,
            trackNumber = trackNumber,
            durationSeconds = durationSeconds,
            albumName = albumName,
            coverArt = coverArt,
            coverArtFilePath = coverArtFilePath,
            starredAt = starredAt,
        )

    private fun TrackEntity.toDomain(): Track =
        Track(
            id = id,
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

    private fun PlaylistEntity.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }
}
