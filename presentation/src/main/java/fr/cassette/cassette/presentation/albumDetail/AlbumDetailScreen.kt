package fr.cassette.cassette.presentation.albumDetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailTrackRow
import fr.cassette.cassette.presentation.core.AlbumCoverArt
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.HomeMessage
import fr.cassette.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel

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
private fun AlbumDetailHeader(
    album: AlbumDetail?,
    coverArt: AlbumCoverArtModel?,
    tracksCount: Int,
    height: androidx.compose.ui.unit.Dp,
    onShuffleClick: () -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height),
    ) {
        AlbumCoverArt(
            coverArt = coverArt,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    surfaceColor.copy(alpha = 0.28f),
                                    surfaceColor.copy(alpha = 0.88f),
                                    surfaceColor,
                                ),
                        ),
                    ),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 96.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = album?.name ?: stringResource(R.string.album_detail_title),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text =
                    albumDetailSubtitle(
                        artist = album?.artist ?: stringResource(R.string.album_detail_unknown_artist),
                        tracksCount = tracksCount,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        FloatingActionButton(
            onClick = onShuffleClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = stringResource(R.string.album_detail_shuffle),
            )
        }
    }
}

@Composable
private fun AlbumDetailBackButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.86f),
            ),
        modifier =
            Modifier
                .statusBarsPadding()
                .padding(start = 12.dp, top = 4.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.album_detail_back),
        )
    }
}

@Composable
private fun albumDetailSubtitle(
    artist: String,
    tracksCount: Int,
): String =
    stringResource(
        R.string.album_detail_meta_line,
        artist,
        pluralStringResource(R.plurals.album_detail_tracks_count, tracksCount, tracksCount),
    )

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
