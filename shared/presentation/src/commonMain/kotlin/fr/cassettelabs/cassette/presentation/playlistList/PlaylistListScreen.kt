package fr.cassettelabs.cassette.presentation.playlistList

import cassette.shared.presentation.generated.resources.playlist_list_title
import cassette.shared.presentation.generated.resources.playlist_list_loading_playlists
import cassette.shared.presentation.generated.resources.playlist_list_empty_title
import cassette.shared.presentation.generated.resources.playlist_list_empty_description
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.presentation.core.AppearingEffect
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage
import fr.cassettelabs.cassette.presentation.playlistList.core.PlaylistListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun PlaylistListScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaylistListUiState,
    onEvent: (PlaylistListEvent) -> Unit,
) {
    AppearingEffect {
        onEvent(PlaylistListEvent.OnAppearing)
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Text(
                        text = stringResource(Res.string.playlist_list_title),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
            onRefresh = { onEvent(PlaylistListEvent.OnRefresh) },
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                    state = pullToRefreshState,
                )
            },
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingMessage(
                        message = Res.string.playlist_list_loading_playlists,
                    )
                }
            } else if (uiState.playlists.isEmpty()) {
                HomeMessage(
                    title = stringResource(Res.string.playlist_list_empty_title),
                    description = stringResource(Res.string.playlist_list_empty_description),
                )
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding =
                        contentPadding.plus(
                            PaddingValues(
                                top = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        AnimatedVisibility(uiState.isRefreshing) {
                            LinearWavyProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    items(
                        items = uiState.playlists,
                        key = { playlist -> playlist.id },
                    ) { playlist ->
                        LaunchedEffect(playlist.id) {
                            if (playlist.coverArtFilePath.isNullOrBlank()) {
                                onEvent(PlaylistListEvent.OnPlaylistCoverArtAppeared(playlist.id))
                            }
                        }

                        PlaylistListItem(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            playlist = playlist,
                            coverArtStatus = uiState.playlistCoverArtStatuses[playlist.id],
                            onClick = { onEvent(PlaylistListEvent.OnPlaylistClicked(playlist.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PlaylistListScreenPreview() {
    CassetteTheme {
        PlaylistListScreen(
            uiState =
                PlaylistListUiState(
                    isLoading = false,
                    playlists =
                        listOf(
                            Playlist(
                                id = "1",
                                name = "Workout Mix",
                                trackCount = 42,
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                seedColor = null,
                            ),
                            Playlist(
                                id = "2",
                                name = "Chill Vibes",
                                trackCount = 18,
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                seedColor = null,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}
