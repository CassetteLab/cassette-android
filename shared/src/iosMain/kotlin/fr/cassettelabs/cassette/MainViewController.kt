package fr.cassettelabs.cassette

import androidx.compose.runtime.produceState
import androidx.compose.ui.window.ComposeUIViewController
import fr.cassettelabs.cassette.di.sharedModules
import fr.cassettelabs.cassette.domain.usecases.HasServerConfigurationUseCase
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

fun MainViewController() =
    ComposeUIViewController(
        configure = {
            startKoin {
                modules(sharedModules())
            }
        }
    ) {
        val hasValidServerConfiguration : HasServerConfigurationUseCase = koinInject()
        val isServerConfigurationValid = produceState<Boolean?>(initialValue = null){
            value = hasValidServerConfiguration()
        }.value ?: return@ComposeUIViewController

        App(hasValidServerConfiguration = isServerConfigurationValid)
    }
