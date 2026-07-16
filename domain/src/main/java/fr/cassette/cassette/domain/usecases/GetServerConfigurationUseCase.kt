package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository

class GetServerConfigurationUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(): ServerConfiguration? {
        return serverConfigurationRepository.getServerConfiguration()
    }
}
