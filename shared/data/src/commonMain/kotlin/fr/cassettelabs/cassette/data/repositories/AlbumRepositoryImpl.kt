package fr.cassettelabs.cassette.data.repositories

import fr.cassettelabs.cassette.core.coroutines.CoroutineDispatchers
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.AlbumTrackDao
import fr.cassettelabs.cassette.data.local.dao.ArtistDao
import fr.cassettelabs.cassette.data.local.dao.CoverArtDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.embeddeds.AlbumTrackWithTrack
import fr.cassettelabs.cassette.data.local.embeddeds.AlbumWithCoverArt
import fr.cassettelabs.cassette.data.local.embeddeds.ArtistWithCoverArt
import fr.cassettelabs.cassette.data.local.entities.AlbumTrackEntity
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.ArtistEntity
import fr.cassettelabs.cassette.data.local.entities.CoverArtEntity
import fr.cassettelabs.cassette.data.local.entities.StarredAlbumEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.ktor.currentTimeMillis
import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.StarredLibrary
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class AlbumRepositoryImpl(
    private val logger: Logger,
    private val albumRemoteDataSource: AlbumRemoteDataSourceImpl,
    private val albumDao: AlbumDao,
    private val albumTrackDao: AlbumTrackDao,
    private val artistDao: ArtistDao,
    private val coverArtDao: CoverArtDao,
    private val trackDao: TrackDao,
    private val serverConfigurationDao: ServerConfigurationDao,
    private val coverArtProcessor: CoverArtProcessor,
    private val coroutineDispatchers: CoroutineDispatchers,
) : AlbumRepository {

    init {
        logger.init("AlbumRepositoryImpl")
    }

    override suspend fun getRecentlyAddedAlbums(size: Int): List<Album> =
        albumRemoteDataSource.getRecentlyAddedAlbums(size).map { album ->
            val localAlbum = albumDao.getAlbumWithCoverArt(album.id, COVER_ART_SIZE_KEY)
            val albumWithLocalData =
                album.copy(
                    coverArtFilePath = localAlbum?.validCoverArtFilePath(),
                    seedColor = localAlbum?.seedColor,
                )
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
            albumWithLocalData
        }

    override suspend fun getStarredLibrary(): StarredLibrary {
        val starredLibrary = albumRemoteDataSource.getStarredLibrary()
        val serverConfigurationId = currentServerConfigurationId()

        starredLibrary.albums.forEach { album ->
            val localAlbum = albumDao.getAlbumWithCoverArt(album.id, COVER_ART_SIZE_KEY)
            val albumWithLocalData = album.withLocalAlbumData(localAlbum)
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
        return starredLibrary
    }

    override suspend fun setAlbumStarred(
        albumId: AlbumId,
        isStarred: Boolean,
    ) {
        albumRemoteDataSource.setAlbumStarred(albumId = albumId, isStarred = isStarred)
        albumDao.upsertStarredAlbum(
            StarredAlbumEntity(
                albumId = albumId,
                starredAt = currentTimeMillis().toString().takeIf { isStarred },
            ),
        )
    }

    override suspend fun getAlbum(albumId: AlbumId): Album? {
        val localAlbum = albumDao.getAlbumWithCoverArt(albumId, COVER_ART_SIZE_KEY)
        if (localAlbum == null) {
            logger.w("Can't find album with id: $albumId")
            return null
        }

        return localAlbum.toDomain()
    }

    override suspend fun refreshAlbum(albumId: AlbumId): Album {
        val localAlbum = albumDao.getAlbumWithCoverArt(albumId, COVER_ART_SIZE_KEY)
        val album = albumRemoteDataSource.getAlbum(albumId)
        val albumWithLocalData = album.withLocalAlbumData(localAlbum)
        albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
        return albumWithLocalData
    }

    override suspend fun getAlbumTracks(albumId: String): List<Track> {
        val localTracks = albumTrackDao.getAlbumTracks(albumId, COVER_ART_SIZE_KEY)
        if (localTracks.isNotEmpty()) {
            return localTracks.map { it.toDomain() }
        }

        val localAlbum = albumDao.getAlbumWithCoverArt(albumId, COVER_ART_SIZE_KEY)
        val (remoteAlbum, remoteTracks) = albumRemoteDataSource.getAlbumWithTracks(albumId)
        val albumWithLocalData = remoteAlbum.withLocalAlbumData(localAlbum)
        albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId))
        trackDao.insertTracks(remoteTracks.map { track -> track.toEntity() })
        albumTrackDao.replaceAlbumTracks(albumId, remoteTracks.map { track -> track.toAlbumTrackEntity(albumId) })
        return albumTrackDao.getAlbumTracks(albumId, COVER_ART_SIZE_KEY).map { it.toDomain() }
    }

    override suspend fun refreshAlbumTracks(albumId: AlbumId): List<Track> {
        val remoteTracks = albumRemoteDataSource.getAlbumTracks(albumId)
        trackDao.insertTracks(remoteTracks.map { track -> track.toEntity() })
        albumTrackDao.replaceAlbumTracks(albumId, remoteTracks.map { track -> track.toAlbumTrackEntity(albumId) })
        return albumTrackDao.getAlbumTracks(albumId, COVER_ART_SIZE_KEY).map { it.toDomain() }
    }

    override fun getAlbumCoverArt(
        coverArtId: String,
        size: Int?,
        albumId: String?,
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
                albumId?.let { id ->
                    extractAndSaveSeedColor(id, coverArt.filePath)
                }
            }
        emit(CoverArtLoadingStatus.Loaded(coverArt.filePath))
    }.flowOn(coroutineDispatchers.io).catch { throwable ->
        emit(CoverArtLoadingStatus.Error(throwable))
    }

    private suspend fun extractAndSaveSeedColor(
        albumId: String,
        filePath: String,
    ) {
        val existingSeedColor = albumDao.getAlbum(albumId)?.seedColor
        if (existingSeedColor != null) return

        try {
            coverArtProcessor.extractSeedColor(filePath)?.let { seedColor ->
                albumDao.updateSeedColor(albumId, seedColor)
            }
        } catch (_: Exception) {
        }
    }

    override fun getAllAlbums(): Flow<List<Album>> =
        albumDao.getAllAlbums(COVER_ART_SIZE_KEY).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun refreshAlbums() {
        val remoteAlbums = albumRemoteDataSource.getAllAlbums(size = ALL_ALBUMS_SIZE)
        val serverConfigurationId = currentServerConfigurationId()
        remoteAlbums.forEach { album ->
            val localAlbum = albumDao.getAlbumWithCoverArt(album.id, COVER_ART_SIZE_KEY)
            val albumWithLocalData =
                album.withLocalAlbumData(localAlbum)
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
    }

    override suspend fun getArtist(artistId: String): Artist? {
        val localArtist = artistDao.getArtistWithCoverArt(artistId, COVER_ART_SIZE_KEY)
        if (localArtist != null) {
            return localArtist.toDomain()
        }

        return refreshArtist(artistId)
    }

    override suspend fun refreshArtist(artistId: String): Artist {
        val localArtist = artistDao.getArtistWithCoverArt(artistId, COVER_ART_SIZE_KEY)
        val (artist, albums) = albumRemoteDataSource.getArtistWithAlbums(artistId)
        val artistWithLocalData = artist.copy(coverArtFilePath = localArtist?.validCoverArtFilePath())
        artistDao.insertArtist(artistWithLocalData.toEntity(localArtist?.serverConfigurationId))
        insertAlbums(albums)
        return artistWithLocalData
    }

    override suspend fun getArtistAlbums(artistId: String): List<Album> {
        val localAlbums = albumDao.getArtistAlbums(artistId, COVER_ART_SIZE_KEY)
        if (localAlbums.isNotEmpty()) {
            return localAlbums.map { it.toDomain() }
        }

        val (_, albums) = albumRemoteDataSource.getArtistWithAlbums(artistId)
        insertAlbums(albums)
        return albums
    }

    private suspend fun insertAlbums(albums: List<Album>) {
        val serverConfigurationId = currentServerConfigurationId()
        albums.forEach { album ->
            val localAlbum = albumDao.getAlbumWithCoverArt(album.id, COVER_ART_SIZE_KEY)
            val albumWithLocalData = album.withLocalAlbumData(localAlbum)
            albumDao.insertAlbum(albumWithLocalData.toEntity(localAlbum?.serverConfigurationId ?: serverConfigurationId))
        }
    }

    private fun Album.withLocalAlbumData(localAlbum: AlbumWithCoverArt?): Album {
        val canReuseLocalArtworkData = localAlbum?.coverArt == coverArt
        return copy(
            coverArtFilePath = localAlbum?.validCoverArtFilePath().takeIf { canReuseLocalArtworkData },
            seedColor = localAlbum?.seedColor.takeIf { canReuseLocalArtworkData },
        )
    }

    private suspend fun Album.toEntity(existingServerConfigurationId: Long?): AlbumEntity =
        AlbumEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            artist = artist,
            artistId = artistId,
            coverArt = coverArt,
            created = created,
            seedColor = seedColor,
        )

    private fun AlbumWithCoverArt.toDomain(): Album =
        Album(
            id = id,
            name = name,
            artist = artist,
            artistId = artistId,
            coverArt = coverArt,
            coverArtFilePath = validCoverArtFilePath(),
            created = created,
            seedColor = seedColor,
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

    private fun Track.toAlbumTrackEntity(albumId: String): AlbumTrackEntity =
        AlbumTrackEntity(
            albumId = albumId,
            trackId = id,
            trackNumber = trackNumber,
        )

    private fun AlbumTrackWithTrack.toDomain(): Track =
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

    private fun AlbumEntity.validCoverArtFilePath(): String? =
        null

    private fun AlbumWithCoverArt.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }

    private suspend fun Artist.toEntity(existingServerConfigurationId: Long?): ArtistEntity =
        ArtistEntity(
            id = id,
            serverConfigurationId = existingServerConfigurationId ?: currentServerConfigurationId(),
            name = name,
            albumCount = albumCount,
            coverArt = coverArt,
        )

    private fun ArtistWithCoverArt.toDomain(): Artist =
        Artist(
            id = id,
            name = name,
            albumCount = albumCount,
            coverArt = coverArt,
            coverArtFilePath = validCoverArtFilePath(),
        )

    private fun ArtistWithCoverArt.validCoverArtFilePath(): String? =
        coverArtFilePath?.takeIf { filePath -> coverArtProcessor.fileExists(filePath) }

    private fun CoverArtEntity.validFilePath(): String? =
        filePath.takeIf { coverArtProcessor.fileExists(it) }

    private fun Int?.toCoverArtSizeKey(): Int = this ?: ORIGINAL_COVER_ART_SIZE_KEY

    private companion object {
        const val ALL_ALBUMS_SIZE = 500
        const val COVER_ART_SIZE_KEY = 900
        const val ORIGINAL_COVER_ART_SIZE_KEY = -1
    }
}
