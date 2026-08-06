package fr.cassettelabs.cassette.presentation.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassettelabs.cassette.presentation.main.MainScreen
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteEvent
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteScreen
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingServerConfiguration.OnBoardingServerConfigurationScreen
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeEvent
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeScreen
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CassetteNavigation(
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
            val uiState by viewModel.uiState.collectAsState()

            OnBoardingWelcomeScreen(
                uiState = uiState,
                onEvent = { event ->
                    viewModel.onEvent(event)
                    when (event) {
                        OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                            navController.navigate(Screens.OnBoardingScreens.OnBoardingScreensServerConfigurationScreen) {
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
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.navigate(Screens.OnBoardingScreens.OnBoardingScreensCompleteScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            }

            OnBoardingServerConfigurationScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }

        composable<Screens.OnBoardingScreens.OnBoardingScreensCompleteScreen> {
            val viewModel = koinViewModel<OnBoardingCompleteViewModel>()
            val uiState by viewModel.uiState.collectAsState()

            OnBoardingCompleteScreen(
                uiState = uiState,
                onEvent = { event ->
                    when (event) {
                        OnBoardingCompleteEvent.OnStartListeningClicked -> {
                            navController.navigate(Screens.Main) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                    viewModel.onEvent(event)
                },
            )
        }

        composable<Screens.Main> {
            MainScreen(
                onLoggedOut = {
                    navController.navigate(Screens.OnBoardingScreens.OnBoardingScreensServerConfigurationScreen) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
            )
        }
    }
}
