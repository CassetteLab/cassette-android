package fr.cassettelabs.cassette.presentation.main

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.home_artists_title
import cassette.shared.presentation.generated.resources.home_downloads_title
import cassette.shared.presentation.generated.resources.home_tracks_title
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fr.cassettelabs.cassette.presentation.albumDetail.AlbumDetailEvent
import fr.cassettelabs.cassette.presentation.albumDetail.AlbumDetailScreen
import fr.cassettelabs.cassette.presentation.albumDetail.AlbumDetailViewModel
import fr.cassettelabs.cassette.presentation.albumList.AlbumListEvent
import fr.cassettelabs.cassette.presentation.albumList.AlbumListScreen
import fr.cassettelabs.cassette.presentation.albumList.AlbumListViewModel
import fr.cassettelabs.cassette.presentation.artistDetail.ArtistDetailEvent
import fr.cassettelabs.cassette.presentation.artistDetail.ArtistDetailScreen
import fr.cassettelabs.cassette.presentation.artistDetail.ArtistDetailViewModel
import fr.cassettelabs.cassette.presentation.core.NowPlayingSnack
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassettelabs.cassette.presentation.core.theme.CassetteBackgroundPrimary
import fr.cassettelabs.cassette.presentation.home.HomeEvent
import fr.cassettelabs.cassette.presentation.home.HomeScreen
import fr.cassettelabs.cassette.presentation.home.HomeViewModel
import fr.cassettelabs.cassette.presentation.libraryPlaceholder.LibraryPlaceholderEvent
import fr.cassettelabs.cassette.presentation.libraryPlaceholder.LibraryPlaceholderScreen
import fr.cassettelabs.cassette.presentation.main.core.MainTab
import fr.cassettelabs.cassette.presentation.nowPlaying.NowPlayingEvent
import fr.cassettelabs.cassette.presentation.nowPlaying.NowPlayingScreen
import fr.cassettelabs.cassette.presentation.nowPlaying.NowPlayingViewModel
import fr.cassettelabs.cassette.presentation.playbackQueue.PlaybackQueueEvent
import fr.cassettelabs.cassette.presentation.playbackQueue.PlaybackQueueScreen
import fr.cassettelabs.cassette.presentation.playbackQueue.PlaybackQueueViewModel
import fr.cassettelabs.cassette.presentation.playlistDetail.PlaylistDetailEvent
import fr.cassettelabs.cassette.presentation.playlistDetail.PlaylistDetailScreen
import fr.cassettelabs.cassette.presentation.playlistDetail.PlaylistDetailViewModel
import fr.cassettelabs.cassette.presentation.playlistList.PlaylistListEvent
import fr.cassettelabs.cassette.presentation.playlistList.PlaylistListScreen
import fr.cassettelabs.cassette.presentation.playlistList.PlaylistListViewModel
import fr.cassettelabs.cassette.presentation.settings.SettingsEvent
import fr.cassettelabs.cassette.presentation.settings.SettingsScreen
import fr.cassettelabs.cassette.presentation.settings.SettingsViewModel
import fr.cassettelabs.cassette.presentation.settings.serverConfiguration.SettingsServerConfigurationScreen
import fr.cassettelabs.cassette.presentation.starred.StarredEvent
import fr.cassettelabs.cassette.presentation.starred.StarredScreen
import fr.cassettelabs.cassette.presentation.starred.StarredViewModel
import navigation.ModalBottomSheetLayout
import navigation.bottomSheet
import navigation.rememberBottomSheetNavigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(onLoggedOut: () -> Unit) {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startDestination = MainTab.Home
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }
    val bottomSheetNavigator = rememberBottomSheetNavigator(skipPartiallyExpanded = true)
    val navController = rememberNavController(bottomSheetNavigator)
    var openPlaybackQueueAfterNowPlayingDismiss by remember { mutableStateOf(false) }
    var openArtistDetailAfterNowPlayingDismiss by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(MainEvent.OnAppearing)
    }

    LaunchedEffect(openPlaybackQueueAfterNowPlayingDismiss, bottomSheetNavigator.sheetEnabled) {
        if (openPlaybackQueueAfterNowPlayingDismiss && !bottomSheetNavigator.sheetEnabled) {
            openPlaybackQueueAfterNowPlayingDismiss = false
            navController.navigate(Screens.PlaybackQueue) {
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(openArtistDetailAfterNowPlayingDismiss, bottomSheetNavigator.sheetEnabled) {
        val artistId = openArtistDetailAfterNowPlayingDismiss
        if (artistId != null && !bottomSheetNavigator.sheetEnabled) {
            openArtistDetailAfterNowPlayingDismiss = null
            navController.navigate(Screens.ArtistDetail(artistId = artistId)) {
                launchSingleTop = true
            }
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar =
        currentDestination?.hasRoute<Screens.AlbumDetail>() != true &&
            currentDestination?.hasRoute<Screens.ArtistDetail>() != true &&
            currentDestination?.hasRoute<Screens.PlaylistDetail>() != true &&
            currentDestination?.hasRoute<Screens.SettingsServerConfiguration>() != true &&
            currentDestination?.hasRoute<Screens.NowPlaying>() != true &&
            currentDestination?.hasRoute<Screens.PlaybackQueue>() != true &&
            currentDestination?.hasRoute<Screens.ArtistList>() != true &&
            currentDestination?.hasRoute<Screens.TrackList>() != true &&
            currentDestination?.hasRoute<Screens.Downloads>() != true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        bottomBar = {
            Column {
                AnimatedVisibility(visible = showBottomBar) {
                    BottomAppBar(
                        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                        containerColor = CassetteBackgroundPrimary
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
                }
            }
        },
    ) { contentPadding ->
        val mainContentPadding = if (showBottomBar) contentPadding else PaddingValues()
        val nowPlayingSnackPadding = if (showBottomBar) PaddingValues() else contentPadding

        Box(
            modifier = Modifier.padding(mainContentPadding),
        ) {
            val subScreenContentPadding = remember(uiState.currentTrack) {
                if (uiState.currentTrack != null)
                    PaddingValues(bottom = 76.dp)
                else
                    PaddingValues()
            }

            ModalBottomSheetLayout(
                modifier =
                    Modifier
                        .fillMaxSize(),
                dragHandle = null,
                bottomSheetNavigator = bottomSheetNavigator,
                contentWindowInsets = { WindowInsets(0.dp) },
            ) {
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
                                    HomeEvent.OnArtistsClicked -> {
                                        navController.navigate(Screens.ArtistList) {
                                            launchSingleTop = true
                                        }
                                    }

                                    HomeEvent.OnAlbumsClicked -> {
                                        selectedDestination = MainTab.AlbumList
                                        navController.navigate(Screens.AlbumList) {
                                            launchSingleTop = true
                                        }
                                    }

                                    is HomeEvent.OnAlbumClicked -> {
                                        navController.navigate(Screens.AlbumDetail(albumId = event.albumId)) {
                                            launchSingleTop = true
                                        }
                                    }

                                    is HomeEvent.OnAlbumCoverArtAppeared -> Unit

                                    HomeEvent.OnTracksClicked -> {
                                        navController.navigate(Screens.TrackList) {
                                            launchSingleTop = true
                                        }
                                    }

                                    HomeEvent.OnPlaylistsClicked -> {
                                        selectedDestination = MainTab.PlaylistList
                                        navController.navigate(Screens.PlaylistList) {
                                            launchSingleTop = true
                                        }
                                    }

                                    is HomeEvent.OnPlaylistClicked -> {
                                        navController.navigate(Screens.PlaylistDetail(playlistId = event.playlistId)) {
                                            launchSingleTop = true
                                        }
                                    }

                                    is HomeEvent.OnPlaylistCoverArtAppeared -> Unit

                                    HomeEvent.OnStarredClicked -> {
                                        selectedDestination = MainTab.Starred
                                        navController.navigate(Screens.Starred) {
                                            launchSingleTop = true
                                        }
                                    }

                                    HomeEvent.OnDownloadsClicked -> {
                                        navController.navigate(Screens.Downloads) {
                                            launchSingleTop = true
                                        }
                                    }

                                    HomeEvent.OnQueueClicked -> {
                                        navController.navigate(Screens.PlaybackQueue) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.ArtistList> {
                        LibraryPlaceholderScreen(
                            title = Res.string.home_artists_title,
                            contentPadding = subScreenContentPadding,
                            onEvent = { event ->
                                when (event) {
                                    LibraryPlaceholderEvent.OnBackClicked -> navController.navigateUp()
                                }
                            },
                        )
                    }

                    composable<Screens.TrackList> {
                        LibraryPlaceholderScreen(
                            title = Res.string.home_tracks_title,
                            contentPadding = subScreenContentPadding,
                            onEvent = { event ->
                                when (event) {
                                    LibraryPlaceholderEvent.OnBackClicked -> navController.navigateUp()
                                }
                            },
                        )
                    }

                    composable<Screens.Downloads> {
                        LibraryPlaceholderScreen(
                            title = Res.string.home_downloads_title,
                            contentPadding = subScreenContentPadding,
                            onEvent = { event ->
                                when (event) {
                                    LibraryPlaceholderEvent.OnBackClicked -> navController.navigateUp()
                                }
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
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    is PlaylistListEvent.OnPlaylistClicked -> {
                                        navController.navigate(Screens.PlaylistDetail(playlistId = event.playlistId)) {
                                            launchSingleTop = true
                                        }
                                    }

                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.Starred> {
                        val viewModel: StarredViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        StarredScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    is StarredEvent.OnAlbumClicked -> {
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

                    composable<Screens.Settings> {
                        val viewModel: SettingsViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(uiState.isLoggedOut) {
                            if (uiState.isLoggedOut) {
                                onLoggedOut()
                            }
                        }

                        SettingsScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
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
                                    is AlbumDetailEvent.OnArtistClicked -> {
                                        navController.navigate(Screens.ArtistDetail(artistId = event.artistId)) {
                                            launchSingleTop = true
                                        }
                                    }
                                    else -> Unit
                                }
                                viewModel.onEvent(event)
                            },
                        )
                    }

                    composable<Screens.ArtistDetail> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screens.ArtistDetail>()
                        val viewModel =
                            koinViewModel<ArtistDetailViewModel>(
                                parameters = { parametersOf(route.artistId) },
                            )
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        ArtistDetailScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    ArtistDetailEvent.OnBackClicked -> navController.navigateUp()
                                    is ArtistDetailEvent.OnAlbumClicked -> {
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

                    composable<Screens.PlaylistDetail> { backStackEntry ->
                        val route = backStackEntry.toRoute<Screens.PlaylistDetail>()
                        val viewModel =
                            koinViewModel<PlaylistDetailViewModel>(
                                parameters = { parametersOf(route.playlistId) },
                            )
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        PlaylistDetailScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    PlaylistDetailEvent.OnBackClicked -> navController.navigateUp()
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

                    composable<Screens.PlaybackQueue> {
                        val viewModel = koinViewModel<PlaybackQueueViewModel>()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        PlaybackQueueScreen(
                            contentPadding = subScreenContentPadding,
                            uiState = uiState,
                            onEvent = { event ->
                                when (event) {
                                    PlaybackQueueEvent.OnBackClicked -> navController.navigateUp()
                                    else -> Unit
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
                                    NowPlayingEvent.OnBackClicked -> navController.navigateUp()
                                    NowPlayingEvent.OnQueueClicked -> {
                                        openPlaybackQueueAfterNowPlayingDismiss = true
                                        navController.navigateUp()
                                    }
                                    is NowPlayingEvent.OnArtistClicked -> {
                                        openArtistDetailAfterNowPlayingDismiss = event.artistId
                                        navController.navigateUp()
                                    }
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
                            .padding(nowPlayingSnackPadding)
                            .padding(12.dp),
                    track = uiState.currentTrack?.title ?: "",
                    artist = uiState.currentTrack?.artist ?: "",
                    coverArtFilePath = uiState.coverArtFilePath,
                    coverArtStatus = uiState.coverArtStatus,
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
