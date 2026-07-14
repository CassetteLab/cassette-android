package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository

class SaveServerConfigurationUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(serverConfiguration: ServerConfiguration) {
        serverConfigurationRepository.saveServerConfiguration(serverConfiguration)
    }
}
