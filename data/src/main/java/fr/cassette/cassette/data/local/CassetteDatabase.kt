package fr.cassette.cassette.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.cassette.cassette.data.local.dao.AlbumDao
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.dao.TrackDao
import fr.cassette.cassette.data.local.entities.AlbumEntity
import fr.cassette.cassette.data.local.entities.FavoriteAlbumEntity
import fr.cassette.cassette.data.local.entities.FavoriteTrackEntity
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
    ],
    version = 3,
    exportSchema = false,
)
internal abstract class CassetteDatabase : RoomDatabase() {
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun serverConfigurationDao(): ServerConfigurationDao
}
