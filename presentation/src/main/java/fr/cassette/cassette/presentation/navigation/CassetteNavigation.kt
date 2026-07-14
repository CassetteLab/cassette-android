package fr.cassette.cassette.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationScreen
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeEvent
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CassetteNavigation(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<CassetteRoute>(CassetteRoute.OnBoardingWelcome) }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<CassetteRoute.OnBoardingWelcome> {
                val viewModel = koinViewModel<OnBoardingWelcomeViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                OnBoardingWelcomeScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                                backStack.add(CassetteRoute.ServerConfiguration)
                            }

                            else -> viewModel.onEvent(event)
                        }
                    },
                )
            }

            entry<CassetteRoute.ServerConfiguration> {
                val viewModel = koinViewModel<ServerConfigurationViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                ServerConfigurationScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            ServerConfigurationEvent.OnConnectClicked -> {
                                viewModel.onEvent(event)
                                backStack.add(CassetteRoute.OnBoardingCache)
                            }

                            else -> viewModel.onEvent(event)
                        }
                    },
                )
            }

            entry<CassetteRoute.OnBoardingCache> {
                val viewModel = koinViewModel<OnBoardingCacheViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                OnBoardingCacheScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                )
            }
        },
    )
}

private sealed interface CassetteRoute : NavKey {
    data object OnBoardingWelcome : CassetteRoute
    data object ServerConfiguration : CassetteRoute
    data object OnBoardingCache : CassetteRoute
}
