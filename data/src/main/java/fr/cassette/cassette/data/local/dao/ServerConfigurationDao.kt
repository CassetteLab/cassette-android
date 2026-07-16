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
    suspend fun insertCustomHeaders(customHeaders: List<ServerConfigurationCustomHeaderEntity>)

    @Transaction
    suspend fun insertServerConfigurationWithCustomHeaders(
        serverConfiguration: ServerConfigurationEntity,
        customHeaders: List<ServerConfigurationCustomHeaderEntity>,
    ): Long {
        deleteCustomHeaders()
        deleteServerConfigurations()
        val serverConfigurationId = insertServerConfiguration(serverConfiguration)
        if (customHeaders.isNotEmpty()) {
            insertCustomHeaders(
                customHeaders.map { customHeader ->
                    customHeader.copy(serverConfigurationId = serverConfigurationId)
                },
            )
        }
        check(countServerConfigurations() == 1) { "Only one server configuration can exist" }
        return serverConfigurationId
    }

    @Query("DELETE FROM server_configuration_custom_headers")
    suspend fun deleteCustomHeaders()

    @Query("DELETE FROM server_configurations")
    suspend fun deleteServerConfigurations()

    @Transaction
    @Query("SELECT * FROM server_configurations ORDER BY id ASC LIMIT 1")
    suspend fun getFirstServerConfiguration(): ServerConfigurationWithCustomHeaders?

    @Transaction
    suspend fun getServerConfiguration(): ServerConfigurationWithCustomHeaders? {
        check(countServerConfigurations() <= 1) { "Only one server configuration can exist" }
        return getFirstServerConfiguration()
    }

    @Query("SELECT COUNT(*) FROM server_configurations")
    suspend fun countServerConfigurations(): Int

    suspend fun hasServerConfiguration(): Boolean {
        val count = countServerConfigurations()
        check(count <= 1) { "Only one server configuration can exist" }
        return count == 1
    }
}
