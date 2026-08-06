package fr.cassettelabs.cassette.domain.usecases.configuration

import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository

class LogoutUseCase(
    private val serverConfigurationRepository: ServerConfigurationRepository,
) {
    suspend operator fun invoke() {
        serverConfigurationRepository.logout()
    }
}
