package fr.cassette.cassette.presentation.playlistDetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.PlaylistDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassette.cassette.presentation.core.LoadingMessage
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.playlistDetail.core.PlaylistDetailHeader
import fr.cassette.cassette.presentation.playlistDetail.core.PlaylistDetailTrackItem

@Composable
internal fun PlaylistDetailScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaylistDetailUiState,
    onEvent: (PlaylistDetailEvent) -> Unit,
) {
    var playlistArtBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uiState.coverArt) {
        playlistArtBitmap =
            uiState.coverArt?.let { coverArt ->
                BitmapFactory.decodeFile(coverArt.filePath)
            }
    }

    LaunchedEffect(Unit) {
        onEvent(PlaylistDetailEvent.OnAppearing)
    }

    AlbumArtworkTheme(albumArt = playlistArtBitmap) {
        when {
            uiState.isLoading && uiState.playlist == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingMessage(
                        message = R.string.playlist_detail_loading_message,
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
                        contentPadding =
                            contentPadding.plus(
                                other =
                                    PaddingValues(
                                        bottom = 16.dp,
                                    ),
                            ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            PlaylistDetailHeader(
                                playlist = uiState.playlist,
                                coverArt = uiState.coverArt,
                                tracksCount = uiState.tracks.size,
                                height = maxHeaderHeight,
                                onShuffleClick = {
                                    uiState.tracks.randomOrNull()?.let { track ->
                                        onEvent(PlaylistDetailEvent.OnTrackClicked(track.id))
                                    }
                                },
                            )
                        }

                        if (uiState.isTracksLoading) {
                            item {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(top = 32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    LoadingMessage(
                                        message = R.string.playlist_detail_tracks_loading_message,
                                    )
                                }
                            }
                        }

                        items(uiState.tracks, key = { track -> track.id }) { track ->
                            PlaylistDetailTrackItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                track = track,
                                onClick = { onEvent(PlaylistDetailEvent.OnTrackClicked(track.id)) },
                            )
                        }
                    }

                    AlbumDetailBackButton(onClick = { onEvent(PlaylistDetailEvent.OnBackClicked) })
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PlaylistDetailScreenPreview() {
    CassetteTheme {
        PlaylistDetailScreen(
            uiState =
                PlaylistDetailUiState(
                    playlistId = "playlist-1",
                    playlist =
                        PlaylistDetail(
                            id = "playlist-1",
                            name = "Workout Mix",
                            trackCount = 2,
                            coverArt = "pl-123",
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
                ),
            onEvent = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun PlaylistDetailScreenLoadingPreview() {
    CassetteTheme {
        PlaylistDetailScreen(
            uiState =
                PlaylistDetailUiState(
                    playlistId = "playlist-1",
                    isLoading = true,
                ),
            onEvent = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun PlaylistDetailScreenTracksLoadingPreview() {
    CassetteTheme {
        PlaylistDetailScreen(
            uiState =
                PlaylistDetailUiState(
                    playlistId = "playlist-1",
                    isTracksLoading = true,
                ),
            onEvent = {},
        )
    }
}
