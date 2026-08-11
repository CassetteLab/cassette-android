package fr.cassettelabs.cassette.presentation.playlistDetail

import cassette.shared.presentation.generated.resources.playlist_detail_loading_message
import cassette.shared.presentation.generated.resources.playlist_detail_tracks_loading_message
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_detail_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.playlistDetail.core.PlaylistDetailBottomSheet
import fr.cassettelabs.cassette.presentation.playlistDetail.core.PlaylistDetailHeader
import fr.cassettelabs.cassette.presentation.playlistDetail.core.PlaylistDetailTrackItem
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlaylistDetailScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaylistDetailUiState,
    onEvent: (PlaylistDetailEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(PlaylistDetailEvent.OnAppearing)
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    AlbumArtworkTheme(albumArt = (uiState.coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath) {
        when {
            uiState.isLoading && uiState.playlist == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingMessage(
                        message = Res.string.playlist_detail_loading_message,
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
                                coverArtStatus = uiState.coverArtStatus,
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
                                        message = Res.string.playlist_detail_tracks_loading_message,
                                    )
                                }
                            }
                        }

                        itemsIndexed(uiState.tracks, key = { index, track -> "${track.id}-$index" }) { _, track ->
                            PlaylistDetailTrackItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                track = track,
                                coverArtStatus = uiState.trackCoverArtStatuses[track.id],
                                onClick = { onEvent(PlaylistDetailEvent.OnTrackClicked(track.id)) },
                            )
                        }
                    }

                    AlbumDetailBackButton(onClick = { onEvent(PlaylistDetailEvent.OnBackClicked) })

                    FilledIconButton(
                        onClick = { showBottomSheet = true },
                        colors =
                            IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.86f),
                            ),
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(end = 12.dp, top = 4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(Res.string.playlist_detail_menu),
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            PlaylistDetailBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                onDeleteClick = {
                    onEvent(PlaylistDetailEvent.OnDeletePlaylist)
                },
            )
        }
    }
}

@Composable
@Preview
private fun PlaylistDetailScreenPreview() {
    CassetteTheme {
        PlaylistDetailScreen(
            uiState =
                PlaylistDetailUiState(
                    playlistId = "playlist-1",
                    playlist =
                        Playlist(
                            id = "playlist-1",
                            name = "Workout Mix",
                            trackCount = 2,
                            coverArt = "pl-123",
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
                ),
            onEvent = {},
        )
    }
}

@Composable
@Preview
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
@Preview
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
