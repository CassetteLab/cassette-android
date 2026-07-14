package fr.cassette.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassette.cassette.data.local.entity.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entity.ServerConfigurationEntity
import fr.cassette.cassette.data.local.model.ServerConfigurationWithCustomHeaders
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ServerConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerConfiguration(serverConfiguration: ServerConfigurationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomHeader(customHeader: ServerConfigurationCustomHeaderEntity): Long

    @Transaction
    @Query("SELECT * FROM server_configurations ORDER BY id ASC")
    fun observeServerConfigurations(): Flow<List<ServerConfigurationWithCustomHeaders>>
}
