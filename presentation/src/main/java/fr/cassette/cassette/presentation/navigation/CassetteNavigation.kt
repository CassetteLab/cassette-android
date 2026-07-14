package fr.cassette.cassette.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CassetteRoute.OnBoardingWelcome,
        modifier = modifier,
    ) {
        composable(CassetteRoute.OnBoardingWelcome) {
            val viewModel = koinViewModel<OnBoardingWelcomeViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnBoardingWelcomeScreen(
                uiState = uiState,
                onEvent = { event ->
                    when (event) {
                        OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                            navController.navigate(CassetteRoute.ServerConfiguration)
                        }

                        else -> viewModel.onEvent(event)
                    }
                },
            )
        }

        composable(CassetteRoute.ServerConfiguration) {
            val viewModel = koinViewModel<ServerConfigurationViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ServerConfigurationScreen(
                uiState = uiState,
                onEvent = { event ->
                    when (event) {
                        ServerConfigurationEvent.OnConnectClicked -> {
                            viewModel.onEvent(event)
                            navController.navigate(CassetteRoute.OnBoardingCache)
                        }

                        else -> viewModel.onEvent(event)
                    }
                },
            )
        }

        composable(CassetteRoute.OnBoardingCache) {
            val viewModel = koinViewModel<OnBoardingCacheViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnBoardingCacheScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }
    }
}

private object CassetteRoute {
    const val OnBoardingWelcome = "on_boarding_welcome"
    const val ServerConfiguration = "server_configuration"
    const val OnBoardingCache = "on_boarding_cache"
}
