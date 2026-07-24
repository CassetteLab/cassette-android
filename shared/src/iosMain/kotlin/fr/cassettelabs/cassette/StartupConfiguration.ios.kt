package fr.cassettelabs.cassette

import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.dsl.module

private val startupScope = MainScope()

fun hasValidServerConfiguration(callback: (Boolean) -> Unit) {
    startupScope.launch {
        callback(
            hasValidServerConfiguration(
                platformModule =
                    module {
                        single { DatabaseBuilderFactory() }
                    },
            ),
        )
    }
}
