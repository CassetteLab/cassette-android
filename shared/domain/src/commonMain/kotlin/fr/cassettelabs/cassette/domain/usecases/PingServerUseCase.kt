package fr.cassettelabs.cassette.domain.usecases

import fr.cassettelabs.cassette.domain.models.ServerConfiguration
import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository

class PingServerUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(serverConfiguration: ServerConfiguration) {
        serverConfigurationRepository.pingServer(serverConfiguration)
    }
}
