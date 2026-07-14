package fr.cassette.cassette.data.repositories

import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.local.entities.ServerConfigurationCustomHeaderEntity
import fr.cassette.cassette.data.local.entities.ServerConfigurationEntity
import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository

internal class ServerConfigurationRepositoryImpl(
    private val serverConfigurationDao: ServerConfigurationDao,
) : ServerConfigurationRepository {
    override suspend fun saveServerConfiguration(serverConfiguration: ServerConfiguration) {
        serverConfigurationDao.insertServerConfigurationWithCustomHeaders(
            serverConfiguration = ServerConfigurationEntity(
                serverUrl = serverConfiguration.serverUrl,
                username = serverConfiguration.username,
                password = serverConfiguration.password,
            ),
            customHeaders = serverConfiguration.customHeaders.map { customHeader ->
                ServerConfigurationCustomHeaderEntity(
                    serverConfigurationId = 0,
                    name = customHeader.name,
                    value = customHeader.value,
                )
            },
        )
    }
}
