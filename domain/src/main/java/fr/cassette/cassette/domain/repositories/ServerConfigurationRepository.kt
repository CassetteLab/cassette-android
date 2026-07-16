package fr.cassette.cassette.domain.repositories

import fr.cassette.cassette.domain.models.ServerConfiguration

interface ServerConfigurationRepository {
    suspend fun pingServer(serverConfiguration: ServerConfiguration)

    suspend fun getServerConfiguration(): ServerConfiguration?

    suspend fun saveServerConfiguration(serverConfiguration: ServerConfiguration)

    suspend fun hasServerConfiguration(): Boolean
}
