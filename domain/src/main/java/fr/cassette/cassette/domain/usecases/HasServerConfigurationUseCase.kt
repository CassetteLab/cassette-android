package fr.cassette.cassette.domain.usecases

import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository

class HasServerConfigurationUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke(): Boolean {
        return serverConfigurationRepository.hasServerConfiguration()
    }
}
