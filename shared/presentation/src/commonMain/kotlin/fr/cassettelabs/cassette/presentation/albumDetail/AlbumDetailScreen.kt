package fr.cassettelabs.cassette.presentation.albumDetail

import cassette.shared.presentation.generated.resources.album_detail_loading_message
import cassette.shared.presentation.generated.resources.album_detail_tracks_loading_message
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailHeader
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailTrackRow
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun AlbumDetailScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: AlbumDetailUiState,
    onEvent: (AlbumDetailEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(AlbumDetailEvent.OnAppearing)
    }

    AlbumArtworkTheme(albumArt = uiState.coverArt?.filePath) {
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

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
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
                                coverArt = uiState.coverArt,
                                tracksCount = uiState.tracks.size,
                                height = maxHeaderHeight,
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
                            AlbumDetailTrackRow(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                track = track,
                                isCurrentTrack = track.id == uiState.currentTrackId,
                                isPlaying = uiState.isPlaying,
                                onClick = { onEvent(AlbumDetailEvent.OnTrackClicked(track.id)) },
                            )
                        }
                    }

                    AlbumDetailBackButton(onClick = { onEvent(AlbumDetailEvent.OnBackClicked) })
                }
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
                    albumId = "2YuwDgPuXhF5ir4SjAl6Iw",
                    album =
                        AlbumDetail(
                            id = "2YuwDgPuXhF5ir4SjAl6Iw",
                            name = "Discovery",
                            artist = "Daft Punk",
                            coverArt = "al-123",
                            coverArtFilePath = null,
                            created = "2026-07-15T12:00:00",
                            tracks = emptyList(),
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
                    albumId = "2YuwDgPuXhF5ir4SjAl6Iw",
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
                    albumId = "2YuwDgPuXhF5ir4SjAl6Iw",
                    isTracksLoading = true
                ),
            onEvent = {},
        )
    }
}
