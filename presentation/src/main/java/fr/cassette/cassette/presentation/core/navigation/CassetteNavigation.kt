package fr.cassette.cassette.presentation.core.navigation

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
    startDestination: Screens,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Screens.OnBoardingScreens.OnBoardingScreensWelcomeScreen> {
            val viewModel = koinViewModel<OnBoardingWelcomeViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnBoardingWelcomeScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    when (event) {
                        OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                            navController.navigate(Screens.OnBoardingScreens.OnBoardingScreensServerConfigurationScreen){
                                popUpTo(navController.graph.id)
                            }
                        }
                        else -> Unit
                    }
                },
            )
        }

        composable<Screens.OnBoardingScreens.OnBoardingScreensServerConfigurationScreen> {
            val viewModel = koinViewModel<ServerConfigurationViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.navigate(Screens.OnBoardingScreens.OnBoardingScreensCompleteScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            }

            ServerConfigurationScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }

        composable<Screens.OnBoardingScreens.OnBoardingScreensCompleteScreen> {
            val viewModel = koinViewModel<OnBoardingCompleteViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnBoardingCompleteScreen(
                uiState = uiState,
                onEvent = { event ->
                    when (event) {
                        OnBoardingCompleteEvent.OnStartListeningClicked -> {
                            navController.navigate(Screens.Home) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                    viewModel.onEvent(event)
                },
            )
        }

        composable<Screens.Home> {
            HomeScreen()
        }
    }
}