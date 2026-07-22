package fr.cassette.cassette.presentation.albumDetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailHeader
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailTrackRow
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.HomeMessage

@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    onEvent: (AlbumDetailEvent) -> Unit,
) {
    var albumArtBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uiState.coverArt) {
        albumArtBitmap =
            uiState.coverArt?.let { coverArt ->
                BitmapFactory.decodeFile(coverArt.filePath)
            }
    }

    LaunchedEffect(Unit) {
        onEvent(AlbumDetailEvent.OnAppearing)
    }

    AlbumArtworkTheme(albumArt = albumArtBitmap) {
        when {
            uiState.isLoading && uiState.album == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.hasError && uiState.album == null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    HomeMessage(
                        title = stringResource(R.string.album_detail_error_title),
                        description = stringResource(R.string.album_detail_error_description),
                        actionLabel = stringResource(R.string.album_detail_retry),
                        onActionClick = { onEvent(AlbumDetailEvent.OnRetryClicked) },
                    )
                }
            }

            else -> {
                val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
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
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = navigationBarHeight + 24.dp,
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
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        items(uiState.tracks, key = { track -> track.id }) { track ->
                            AlbumDetailTrackRow(
                                track = track,
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
@PreviewLightDark
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
                ),
            onEvent = {},
        )
    }
}
