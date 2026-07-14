package fr.cassette.cassette.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationScreen
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.home.HomeScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteEvent
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeEvent
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CassetteNavigation(
    startDestination: CassetteStartDestination,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
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

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.navigate(CassetteRoute.OnBoardingComplete)
                }
            }

            ServerConfigurationScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }

        composable(CassetteRoute.OnBoardingComplete) {
            val viewModel = koinViewModel<OnBoardingCompleteViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnBoardingCompleteScreen(
                uiState = uiState,
                onEvent = { event ->
                    when (event) {
                        OnBoardingCompleteEvent.OnStartListeningClicked -> {
                            navController.navigate(CassetteRoute.Home) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                    viewModel.onEvent(event)
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

        composable(CassetteRoute.Home) {
            HomeScreen()
        }
    }
}

internal enum class CassetteStartDestination(internal val route: String) {
    OnBoardingWelcome(CassetteRoute.OnBoardingWelcome),
    Home(CassetteRoute.Home),
}

private object CassetteRoute {
    const val OnBoardingWelcome = "on_boarding_welcome"
    const val ServerConfiguration = "server_configuration"
    const val OnBoardingComplete = "on_boarding_complete"
    const val OnBoardingCache = "on_boarding_cache"
    const val Home = "home"
}
