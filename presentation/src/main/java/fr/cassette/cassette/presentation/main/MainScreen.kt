package fr.cassette.cassette.presentation.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import fr.cassette.cassette.presentation.albumList.AlbumListEvent
import fr.cassette.cassette.presentation.albumList.AlbumListScreen
import fr.cassette.cassette.presentation.albumList.AlbumListViewModel
import fr.cassette.cassette.presentation.core.NowPlayingSnack
import fr.cassette.cassette.presentation.core.navigation.Screens
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.home.HomeEvent
import fr.cassette.cassette.presentation.home.HomeScreen
import fr.cassette.cassette.presentation.home.HomeViewModel
import fr.cassette.cassette.presentation.main.core.MainTab
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingEvent
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingScreen
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingViewModel
import fr.cassette.cassette.presentation.playlistList.PlaylistListScreen
import fr.cassette.cassette.presentation.playlistList.PlaylistListViewModel
import fr.cassette.cassette.presentation.settings.SettingsEvent
import fr.cassette.cassette.presentation.settings.SettingsScreen
import fr.cassette.cassette.presentation.settings.SettingsViewModel
import fr.cassette.cassette.presentation.settings.serverConfiguration.SettingsServerConfigurationScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ComponentActivity.MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startDestination = MainTab.Home
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }
    val bottomSheetNavigator =
        rememberBottomSheetNavigator(skipPartiallyExpanded = true)
    val navController = rememberNavController(bottomSheetNavigator)

    LaunchedEffect(Unit) {
        viewModel.onEvent(MainEvent.OnAppearing)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        bottomBar = {
            BottomAppBar(
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedDestination,
                            onClick = {
                                if (selectedDestination == tab) return@NavigationBarItem

                                selectedDestination = tab
                                navController.navigate(tab.destination) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.id)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.iconRes,
                                    contentDescription = stringResource(tab.labelRes),
                                )
                            },
                            label = {
                                Text(text = stringResource(tab.labelRes))
                            },
                        )
                    }
                }
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding),
        ) {
            ModalBottomSheetLayout(
                modifier =
                    Modifier
                        .fillMaxSize(),
                dragHandle = null,
                bottomSheetNavigator = bottomSheetNavigator,
                contentWindowInsets = { WindowInsets(0.dp) },
            ) {
                val subScreenContentPadding = remember(uiState.currentTrack) {
                    if (uiState.currentTrack != null)
                        PaddingValues(bottom = 76.dp)
                    else
                        PaddingValues()
                }

                NavHost(
                    startDestination = startDestination.destination,
                    navController = navController,
                ) {
                    composable<Screens.Home> {
                        val viewModel: HomeViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        HomeScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    is HomeEvent.OnAlbumClicked -> {
                                        navController.navigate(Screens.AlbumDetail(albumId = event.albumId)) {
                                            launchSingleTop = true
                                        }
                                    }

                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.AlbumList> {
                        val viewModel: AlbumListViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        AlbumListScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    is AlbumListEvent.OnAlbumClicked -> {
                                        navController.navigate(Screens.AlbumDetail(albumId = event.albumId)) {
                                            launchSingleTop = true
                                        }
                                    }

                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.PlaylistList> {
                        val viewModel: PlaylistListViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        PlaylistListScreen(
                            uiState = uiState,
                            onEvent = { event ->
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.Settings> {
                        val viewModel: SettingsViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        SettingsScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    SettingsEvent.OnServerConfigurationClicked -> {
                                        navController.navigate(Screens.SettingsServerConfiguration) {
                                            launchSingleTop = true
                                        }
                                    }

                                    else -> Unit
                                }

                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.AlbumDetail> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screens.AlbumDetail>()
                        val viewModel =
                            koinViewModel<AlbumDetailViewModel>(
                                parameters = { parametersOf(route.albumId) },
                            )
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        AlbumDetailScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    AlbumDetailEvent.OnBackClicked -> navController.navigateUp()
                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            },
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
                                when (event) {
                                    ServerConfigurationEvent.OnBackClicked -> navController.navigateUp()
                                    else -> {}
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    bottomSheet<Screens.NowPlaying> {
                        val viewModel = koinViewModel<NowPlayingViewModel>()
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

            if (uiState.currentTrack != null) {
                NowPlayingSnack(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                    track = uiState.currentTrack?.track?.title ?: "",
                    artist = uiState.currentTrack?.track?.artist ?: "",
                    coverArtFilePath = uiState.coverArtFilePath,
                    isPlaying = uiState.isPlaying,
                    onPlayPause = {
                        if (uiState.isPlaying) {
                            viewModel.onEvent(MainEvent.OnPauseCurrentTrack)
                        } else {
                            viewModel.onEvent(MainEvent.OnPlayCurrentTrack)
                        }
                    },
                    onNext = {
                        viewModel.onEvent(MainEvent.OnNextTrack)
                    },
                    onExpand = {
                        navController.navigate(Screens.NowPlaying)
                    },
                )
            }
        }
    }
}
