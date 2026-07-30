package fr.cassettelabs.cassette.presentation.starred

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.starred_albums_section_title
import cassette.shared.presentation.generated.resources.starred_empty_description
import cassette.shared.presentation.generated.resources.starred_empty_title
import cassette.shared.presentation.generated.resources.starred_loading_message
import cassette.shared.presentation.generated.resources.starred_title
import cassette.shared.presentation.generated.resources.starred_tracks_section_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.AlbumRow
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage
import fr.cassettelabs.cassette.presentation.playlistDetail.core.PlaylistDetailTrackItem
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StarredScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: StarredUiState,
    onEvent: (StarredEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(StarredEvent.OnAppearing)
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
                        text = stringResource(Res.string.starred_title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
            onRefresh = { onEvent(StarredEvent.OnRefresh) },
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                    state = pullToRefreshState,
                )
            },
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingMessage(message = Res.string.starred_loading_message)
                    }
                }
                uiState.isEmpty -> {
                    HomeMessage(
                        title = stringResource(Res.string.starred_empty_title),
                        description = stringResource(Res.string.starred_empty_description),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            contentPadding.plus(
                                PaddingValues(
                                    top = 16.dp,
                                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                                ),
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (uiState.albums.isNotEmpty()) {
                            item(key = "starred-albums-header") {
                                StarredSectionHeader(text = stringResource(Res.string.starred_albums_section_title))
                            }

                            items(uiState.albums, key = { album -> "album-${album.id}" }) { album ->
                                AlbumRow(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    album = album,
                                    coverArtStatus =
                                        uiState.albumCoverArtStatuses[album.id]
                                            ?: album.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
                                    onClick = { onEvent(StarredEvent.OnAlbumClicked(album.id)) },
                                )
                            }
                        }

                        if (uiState.tracks.isNotEmpty()) {
                            item(key = "starred-tracks-header") {
                                StarredSectionHeader(text = stringResource(Res.string.starred_tracks_section_title))
                            }

                            items(uiState.tracks, key = { track -> "track-${track.id}" }) { track ->
                                PlaylistDetailTrackItem(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    track = track,
                                    coverArtStatus = uiState.trackCoverArtStatuses[track.id],
                                    onClick = { onEvent(StarredEvent.OnTrackClicked(track.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StarredSectionHeader(text: String) {
    Text(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
@PreviewLightDark
private fun StarredScreenPreview() {
    CassetteTheme {
        StarredScreen(
            uiState =
                StarredUiState(
                    isLoading = false,
                    albums =
                        listOf(
                            Album(
                                id = "album-1",
                                name = "Discovery",
                                artist = "Daft Punk",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                starredAt = "2026-07-15T12:00:00Z",
                            ),
                        ),
                    tracks =
                        listOf(
                            Track(
                                id = "track-1",
                                title = "One More Time",
                                artist = "Daft Punk",
                                trackNumber = 1,
                                durationSeconds = 320,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun StarredScreenLoadingPreview() {
    CassetteTheme {
        StarredScreen(
            uiState = StarredUiState(isLoading = true),
            onEvent = {},
        )
    }
}
