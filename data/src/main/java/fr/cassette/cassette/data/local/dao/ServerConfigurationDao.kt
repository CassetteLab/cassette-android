package fr.cassette.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassette.cassette.data.local.embeddeds.ServerConfigurationWithCustomHeaders
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ServerConfigurationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerConfiguration(serverConfiguration: ServerConfigurationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomHeader(customHeader: ServerConfigurationCustomHeaderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomHeaders(customHeaders: List<ServerConfigurationCustomHeaderEntity>)

    @Transaction
    suspend fun insertServerConfigurationWithCustomHeaders(
        serverConfiguration: ServerConfigurationEntity,
        customHeaders: List<ServerConfigurationCustomHeaderEntity>,
    ): Long {
        val serverConfigurationId = insertServerConfiguration(serverConfiguration)
        if (customHeaders.isNotEmpty()) {
            insertCustomHeaders(
                customHeaders.map { customHeader ->
                    customHeader.copy(serverConfigurationId = serverConfigurationId)
                },
            )
        }
        return serverConfigurationId
    }

    @Transaction
    @Query("SELECT * FROM server_configurations ORDER BY id ASC")
    fun observeServerConfigurations(): Flow<List<ServerConfigurationWithCustomHeaders>>
}
