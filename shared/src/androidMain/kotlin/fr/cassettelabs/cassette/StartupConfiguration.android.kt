package fr.cassettelabs.cassette

import android.content.Context
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.dsl.module

suspend fun hasValidServerConfiguration(context: Context): Boolean =
    hasValidServerConfiguration(
        platformModule =
            module {
                single { DatabaseBuilderFactory(context.applicationContext) }
            },
    )
