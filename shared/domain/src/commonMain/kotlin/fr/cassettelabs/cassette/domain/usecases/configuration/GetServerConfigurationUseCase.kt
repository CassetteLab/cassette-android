package fr.cassettelabs.cassette.domain.usecases.configuration

import fr.cassettelabs.cassette.domain.models.ServerConfiguration
import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository

class GetServerConfigurationUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(): ServerConfiguration? = serverConfigurationRepository.getServerConfiguration()
}
