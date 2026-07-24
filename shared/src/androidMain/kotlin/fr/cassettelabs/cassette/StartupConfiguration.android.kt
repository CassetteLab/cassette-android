package fr.cassettelabs.cassette

import android.content.Context
import fr.cassettelabs.cassette.core.helpers.AndroidKeystoreCipherHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.dsl.bind
import org.koin.dsl.module

suspend fun hasValidServerConfiguration(context: Context): Boolean =
    hasValidServerConfiguration(
        platformModule =
            module {
                single { DatabaseBuilderFactory(context.applicationContext) }
                single { AndroidKeystoreCipherHelper() } bind CipherHelper::class
            },
    )
