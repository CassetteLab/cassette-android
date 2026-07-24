package fr.cassettelabs.cassette

import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.JvmCipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.dsl.bind
import org.koin.dsl.module

suspend fun hasValidServerConfiguration(): Boolean =
    hasValidServerConfiguration(
        platformModule =
            module {
                single { DatabaseBuilderFactory() }
                single { JvmCipherHelper() } bind CipherHelper::class
            },
    )
