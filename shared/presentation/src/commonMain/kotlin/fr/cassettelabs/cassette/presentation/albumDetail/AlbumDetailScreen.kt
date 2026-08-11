package fr.cassettelabs.cassette.presentation.albumDetail

import cassette.shared.presentation.generated.resources.album_detail_loading_message
import cassette.shared.presentation.generated.resources.album_detail_tracks_loading_message
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailHeader
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailTrackBottomSheet
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailTrackItem
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: AlbumDetailUiState,
    onEvent: (AlbumDetailEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(AlbumDetailEvent.OnAppearing)
    }

    var selectedTrackId by remember { mutableStateOf<String?>(null) }

    val pullToRefreshState = rememberPullToRefreshState()

    AlbumArtworkTheme(albumArt = (uiState.coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath) {
        when {
            uiState.isLoading && uiState.album == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingMessage(
                        message = Res.string.album_detail_loading_message
                    )
                }
            }
            else -> {
                val maxHeaderHeight = 312.dp

                PullToRefreshBox(
                    state = pullToRefreshState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                    isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                    onRefresh = { onEvent(AlbumDetailEvent.OnRefresh) },
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter),
                            isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                            state = pullToRefreshState,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding.plus(
                            other = PaddingValues(
                                bottom = 16.dp
                            )
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            AlbumDetailHeader(
                                album = uiState.album,
                                coverArtStatus = uiState.coverArtStatus,
                                height = maxHeaderHeight,
                                onArtistClick = uiState.album?.artistId?.let { artistId ->
                                    { onEvent(AlbumDetailEvent.OnArtistClicked(artistId)) }
                                },
                                onShuffleClick = {
                                    uiState.tracks.randomOrNull()?.let { track ->
                                        onEvent(AlbumDetailEvent.OnTrackClicked(track.id))
                                    }
                                },
                            )
                        }

                        if (uiState.isTracksLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    LoadingMessage(
                                        message = Res.string.album_detail_tracks_loading_message
                                    )
                                }
                            }
                        }

                        items(uiState.tracks, key = { track -> track.id }) { track ->
                            AlbumDetailTrackItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                track = track,
                                isCurrentTrack = track.id == uiState.currentTrackId,
                                isPlaying = uiState.isPlaying,
                                onClick = { onEvent(AlbumDetailEvent.OnTrackClicked(track.id)) },
                                onMoreClick = { selectedTrackId = track.id },
                            )
                        }
                    }

                    AlbumDetailBackButton(onClick = { onEvent(AlbumDetailEvent.OnBackClicked) })
                }
            }
        }

        selectedTrackId?.let { id ->
            val track = uiState.tracks.firstOrNull { it.id == id }
            if (track != null) {
                AlbumDetailTrackBottomSheet(
                    track = track,
                    coverArtStatus = uiState.coverArtStatus,
                    isLiked = false,
                    onDismiss = { selectedTrackId = null },
                    onLikeClick = {
                        onEvent(AlbumDetailEvent.OnLikeTrack(id))
                        selectedTrackId = null
                    },
                    onAddToPlaylistClick = {
                        onEvent(AlbumDetailEvent.OnAddToPlaylist(id))
                        selectedTrackId = null
                    },
                    onAddToQueueClick = {
                        onEvent(AlbumDetailEvent.OnAddToQueue(id))
                        selectedTrackId = null
                    },
                )
            }
        }
    }
}

@Composable
@Preview
private fun AlbumDetailScreenPreview() {
    CassetteTheme {
        AlbumDetailScreen(
            uiState =
                AlbumDetailUiState(
                    album =
                        Album(
                            id = "2YuwDgPuXhF5ir4SjAl6Iw",
                            name = "Discovery",
                            artist = "Daft Punk",
                            artistId = "artist-1",
                            coverArt = "al-123",
                            coverArtFilePath = null,
                            created = "2026-07-15T12:00:00",
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
                            Track(
                                id = "track-2",
                                title = "Aerodynamic",
                                artist = "Daft Punk",
                                trackNumber = 2,
                                durationSeconds = 212,
                            ),
                        ),
                    currentTrackId = "track-1",
                ),
            onEvent = {},
        )
    }
}

@Composable
@Preview
private fun AlbumDetailScreenLoadingPreview() {
    CassetteTheme {
        AlbumDetailScreen(
            uiState =
                AlbumDetailUiState(
                    isLoading = true
                ),
            onEvent = {},
        )
    }
}

@Composable
@Preview
private fun AlbumDetailScreenTracksLoadingPreview() {
    CassetteTheme {
        AlbumDetailScreen(
            uiState =
                AlbumDetailUiState(
                    isTracksLoading = true
                ),
            onEvent = {},
        )
    }
}
