package fr.cassette.cassette.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity

@Database(
    entities = [
        ServerConfigurationEntity::class,
        ServerConfigurationCustomHeaderEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
internal abstract class CassetteDatabase : RoomDatabase() {
    abstract fun serverConfigurationDao(): ServerConfigurationDao
}
