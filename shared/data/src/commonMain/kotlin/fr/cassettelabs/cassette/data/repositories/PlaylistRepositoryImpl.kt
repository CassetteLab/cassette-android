package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.data.local.dao.PlaylistDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.entities.PlaylistEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.datasources.PlaylistRemoteDataSourceImpl
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.PlaylistDetail
import fr.cassettelabs.cassette.domain.models.PlaylistList
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PlaylistRepositoryImpl(
    private val playlistRemoteDataSource: PlaylistRemoteDataSourceImpl,
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val playlistDao: PlaylistDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<PlaylistList>> =
        playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toListDomain() }
        }

    override suspend fun getPlaylist(playlistId: String): PlaylistDetail {
        val localPlaylist = playlistDao.getPlaylist(playlistId)
        val playlist =
            playlistRemoteDataSource
                .getPlaylist(playlistId)
                .copy(
                    coverArtFilePath = localPlaylist?.validCoverArtFilePath(),
                    seedColor = localPlaylist?.seedColor,
                )
        playlistDao.insertPlaylist(playlist.toEntity(localPlaylist?.serverConfigurationId ?: currentServerConfigurationId()))
        return playlist.copy(tracks = emptyList())
    }

    override suspend fun getPlaylistTracks(playlistId: String): List<Track> = playlistRemoteDataSource.getPlaylistTracks(playlistId)

    override suspend fun refreshPlaylists() {
        val remotePlaylists = playlistRemoteDataSource.getAllPlaylists()
        val serverConfigurationId = currentServerConfigurationId()
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

    override suspend fun getPlaylistCoverArt(
        coverArtId: String,
        size: Int?,
        playlistId: String?,
    ): AlbumCoverArt {
        playlistId?.let { id ->
            playlistDao.getPlaylist(id)?.validCoverArtFilePath()?.let { filePath ->
                return AlbumCoverArt(filePath = filePath)
            }
        }

        return albumRemoteDataSource
            .getAlbumCoverArt(coverArtId = coverArtId, size = size)
            .also { coverArt ->
                playlistId?.let { id ->
                    playlistDao.updateCoverArtFilePath(playlistId = id, coverArtFilePath = coverArt.filePath)
                    extractAndSaveSeedColor(id, coverArt.filePath)
                }
            }
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

    private fun PlaylistList.toEntity(existingServerConfigurationId: Long): PlaylistEntity =
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

    private fun PlaylistDetail.toEntity(existingServerConfigurationId: Long): PlaylistEntity =
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

    private fun PlaylistEntity.toListDomain(): PlaylistList =
        PlaylistList(
            id = id,
            name = name,
            trackCount = trackCount,
            coverArt = coverArt,
            coverArtFilePath = validCoverArtFilePath(),
            created = created,
            seedColor = seedColor,
        )

    private suspend fun currentServerConfigurationId(): Long =
        serverConfigurationDao.getServerConfiguration()?.serverConfiguration?.id
            ?: throw IllegalStateException("No server configuration found")

    private fun PlaylistEntity.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }
}
