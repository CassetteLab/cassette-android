package fr.cassettelabs.cassette.domain.usecases.configuration

import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository

class HasServerConfigurationUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(): Boolean = serverConfigurationRepository.hasServerConfiguration()
}
