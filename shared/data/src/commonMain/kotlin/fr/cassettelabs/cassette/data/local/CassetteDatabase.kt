package fr.cassettelabs.cassette.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.AlbumTrackDao
import fr.cassettelabs.cassette.data.local.dao.ArtistDao
import fr.cassettelabs.cassette.data.local.dao.CoverArtDao
import fr.cassettelabs.cassette.data.local.dao.LocalDataDao
import fr.cassettelabs.cassette.data.local.dao.PlaybackQueueDao
import fr.cassettelabs.cassette.data.local.dao.PlaylistDao
import fr.cassettelabs.cassette.data.local.dao.PlaylistTrackDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.AlbumTrackEntity
import fr.cassettelabs.cassette.data.local.entities.ArtistEntity
import fr.cassettelabs.cassette.data.local.entities.CoverArtEntity
import fr.cassettelabs.cassette.data.local.entities.PlaybackQueueItemEntity
import fr.cassettelabs.cassette.data.local.entities.PlaybackSessionEntity
import fr.cassettelabs.cassette.data.local.entities.PlaylistEntity
import fr.cassettelabs.cassette.data.local.entities.PlaylistTrackEntity
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassettelabs.cassette.data.local.entities.StarredAlbumEntity
import fr.cassettelabs.cassette.data.local.entities.StarredTrackEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity

@Database(
    entities = [
        ServerConfigurationEntity::class,
        ServerConfigurationCustomHeaderEntity::class,
        AlbumEntity::class,
        AlbumTrackEntity::class,
        ArtistEntity::class,
        CoverArtEntity::class,
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        StarredAlbumEntity::class,
        StarredTrackEntity::class,
        PlaybackSessionEntity::class,
        PlaybackQueueItemEntity::class,
    ],
    version = 7,
)
@ConstructedBy(CassetteDatabaseConstructor::class)
internal abstract class CassetteDatabase : RoomDatabase() {
    abstract fun localDataDao(): LocalDataDao

    abstract fun serverConfigurationDao(): ServerConfigurationDao

    abstract fun albumDao(): AlbumDao

    abstract fun albumTrackDao(): AlbumTrackDao

    abstract fun artistDao(): ArtistDao

    abstract fun coverArtDao(): CoverArtDao

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao

    abstract fun playbackQueueDao(): PlaybackQueueDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object CassetteDatabaseConstructor : RoomDatabaseConstructor<CassetteDatabase> {
    override fun initialize(): CassetteDatabase
}
