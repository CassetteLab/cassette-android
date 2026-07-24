package fr.cassettelabs.cassette

import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.dsl.module

suspend fun hasValidServerConfiguration(): Boolean =
    hasValidServerConfiguration(
        platformModule =
            module {
                single { DatabaseBuilderFactory() }
            },
    )
