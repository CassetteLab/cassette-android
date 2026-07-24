package fr.cassettelabs.cassette

import fr.cassettelabs.cassette.di.sharedModules
import fr.cassettelabs.cassette.domain.usecases.HasServerConfigurationUseCase
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

internal suspend fun hasValidServerConfiguration(platformModule: Module): Boolean {
    val koinApplication =
        koinApplication {
            modules(sharedModules() + platformModule)
        }

    return try {
        koinApplication.koin.get<HasServerConfigurationUseCase>()()
    } finally {
        koinApplication.close()
    }
}
