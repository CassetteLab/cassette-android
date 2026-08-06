package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.ServerConfiguration

interface ServerConfigurationRepository {
    suspend fun pingServer(serverConfiguration: ServerConfiguration)

    suspend fun getServerConfiguration(): ServerConfiguration?

    suspend fun saveServerConfiguration(serverConfiguration: ServerConfiguration)

    suspend fun hasServerConfiguration(): Boolean

    suspend fun logout()
}
