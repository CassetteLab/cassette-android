package fr.cassette.cassette.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.cassette.cassette.data.local.dao.AlbumDao
import fr.cassette.cassette.data.local.dao.PlaybackQueueDao
import fr.cassette.cassette.data.local.dao.PlaylistDao
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.dao.TrackDao
import fr.cassette.cassette.data.local.entities.AlbumEntity
import fr.cassette.cassette.data.local.entities.FavoriteAlbumEntity
import fr.cassette.cassette.data.local.entities.FavoriteTrackEntity
import fr.cassette.cassette.data.local.entities.PlaybackQueueItemEntity
import fr.cassette.cassette.data.local.entities.PlaybackSessionEntity
import fr.cassette.cassette.data.local.entities.PlaylistEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassette.cassette.data.local.entities.TrackEntity

@Database(
    entities = [
        ServerConfigurationEntity::class,
        ServerConfigurationCustomHeaderEntity::class,
        AlbumEntity::class,
        TrackEntity::class,
        FavoriteAlbumEntity::class,
        FavoriteTrackEntity::class,
        PlaybackSessionEntity::class,
        PlaybackQueueItemEntity::class,
        PlaylistEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
internal abstract class CassetteDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    abstract fun trackDao(): TrackDao

    abstract fun serverConfigurationDao(): ServerConfigurationDao

    abstract fun playbackQueueDao(): PlaybackQueueDao

    abstract fun playlistDao(): PlaylistDao
}
