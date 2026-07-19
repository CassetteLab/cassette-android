package fr.cassette.cassette.presentation.core.navigation

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stefanoq21.material3.navigation.ModalBottomSheetLayout
import com.stefanoq21.material3.navigation.bottomSheet
import com.stefanoq21.material3.navigation.rememberBottomSheetNavigator
import fr.cassette.cassette.presentation.albumDetail.AlbumDetailEvent
import fr.cassette.cassette.presentation.albumDetail.AlbumDetailScreen
import fr.cassette.cassette.presentation.albumDetail.AlbumDetailViewModel
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.main.MainScreen
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingEvent
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingScreen
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteEvent
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingServerConfiguration.OnBoardingServerConfigurationScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeEvent
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import fr.cassette.cassette.presentation.settings.serverConfiguration.SettingsServerConfigurationScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ComponentActivity.CassetteNavigation(
    startDestination: Screens,
    modifier: Modifier = Modifier,
) {
    val bottomSheetNavigator =
        rememberBottomSheetNavigator(skipPartiallyExpanded = true)
    val navController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(
        modifier = Modifier
            .fillMaxSize(),
        dragHandle = null,
        bottomSheetNavigator = bottomSheetNavigator,
        contentWindowInsets = { WindowInsets(0.dp) }
    ) {
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
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    onNavigateToRootScreen = { screen ->
                        navController.navigate(screen)

                    }
                )
            }

            composable<Screens.SettingsServerConfiguration> {
                val viewModel = koinViewModel<ServerConfigurationViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(uiState.isSaved) {
                    if (uiState.isSaved) {
                        navController.navigateUp()
                    }
                }

                SettingsServerConfigurationScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when(event){
                            ServerConfigurationEvent.OnBackClicked -> navController.navigateUp()
                            else -> {}
                        }
                        viewModel.onEvent(event)
                    }
                )
            }

            composable<Screens.AlbumDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screens.AlbumDetail>()
                val viewModel = koinViewModel<AlbumDetailViewModel>(
                    parameters = { parametersOf(route.albumId) },
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                AlbumDetailScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            AlbumDetailEvent.OnBackClicked -> navController.navigateUp()
                            is AlbumDetailEvent.OnTrackClicked -> {
                                viewModel.onEvent(event)
                                navController.navigate(Screens.NowPlaying(event.trackId))
                                return@AlbumDetailScreen
                            }
                            else -> Unit
                        }
                        viewModel.onEvent(event)
                    },
                )
            }

            bottomSheet<Screens.NowPlaying> { backStackEntry ->
                val route = backStackEntry.toRoute<Screens.NowPlaying>()
                val viewModel = koinViewModel<NowPlayingViewModel>(
                    parameters = { parametersOf(route.trackId) },
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                NowPlayingScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            NowPlayingEvent.OnBackClicked -> onBackPressedDispatcher.onBackPressed()
                            else -> Unit
                        }
                        viewModel.onEvent(event)
                    },
                )
            }
        }
    }
}
