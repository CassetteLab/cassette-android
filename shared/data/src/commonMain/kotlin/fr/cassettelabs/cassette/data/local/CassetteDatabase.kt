package fr.cassettelabs.cassette.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassettelabs.cassette.data.local.entities.ServerConfigurationEntity

@Database(
    entities = [
        ServerConfigurationEntity::class,
        ServerConfigurationCustomHeaderEntity::class,
    ],
    version = 1,
)
@ConstructedBy(CassetteDatabaseConstructor::class)
internal abstract class CassetteDatabase : RoomDatabase() {
    abstract fun serverConfigurationDao(): ServerConfigurationDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object CassetteDatabaseConstructor : RoomDatabaseConstructor<CassetteDatabase> {
    override fun initialize(): CassetteDatabase
}
