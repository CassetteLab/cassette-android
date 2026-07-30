package fr.cassettelabs.cassette.presentation.playbackQueue

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playback_queue_back
import cassette.shared.presentation.generated.resources.playback_queue_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.playbackQueue.core.PlaybackQueueCurrentTrackCard
import fr.cassettelabs.cassette.presentation.playbackQueue.core.PlaybackQueueEmptyState
import fr.cassettelabs.cassette.presentation.playbackQueue.core.PlaybackQueueTrackRow
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaybackQueueScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaybackQueueUiState,
    onEvent: (PlaybackQueueEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(PlaybackQueueEvent.OnAppearing)
    }

    AlbumArtworkTheme(albumArt = uiState.currentTrack?.coverArtFilePath) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    title = {
                        Text(
                            text = stringResource(Res.string.playback_queue_title),
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onEvent(PlaybackQueueEvent.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(Res.string.playback_queue_back),
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            if (uiState.currentTrack == null && uiState.upcomingTracks.isEmpty()) {
                PlaybackQueueEmptyState(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(contentPadding),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        innerPadding.plus(contentPadding).plus(
                            PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    uiState.currentTrack?.let { currentTrack ->
                        item(key = "current-track") {
                            PlaybackQueueCurrentTrackCard(currentTrack = currentTrack)
                        }

                        if (uiState.upcomingTracks.isNotEmpty()) {
                            item(key = "queue-divider") {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                                )
                            }
                        }
                    }

                    itemsIndexed(
                        items = uiState.upcomingTracks,
                        key = { _, track -> track.id },
                    ) { _, currentTrack ->
                        PlaybackQueueTrackRow(
                            currentTrack = currentTrack,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PlaybackQueueScreenPreview() {
    CassetteTheme {
        PlaybackQueueScreen(
            uiState =
                PlaybackQueueUiState(
                    currentTrack =
                        Track(
                            id = "track-3",
                            title = "Aerodynamic",
                            artist = "Daft Punk",
                            trackNumber = 2,
                            durationSeconds = 212,
                            albumId = "album-1",
                            albumName = "Discovery",
                            coverArt = null,
                            coverArtFilePath = null,
                        ),
                    upcomingTracks =
                        listOf(
                            Track(
                                id = "track-1",
                                title = "Aerodynamic",
                                artist = "Daft Punk",
                                trackNumber = 2,
                                durationSeconds = 212,
                                albumId = "album-1",
                                albumName = "Discovery",
                                coverArt = null,
                                coverArtFilePath = null,
                            ),
                            Track(
                                id = "track-2",
                                title = "Digital Love",
                                artist = "Daft Punk",
                                trackNumber = 3,
                                durationSeconds = 301,
                                albumId = "album-1",
                                albumName = "Discovery",
                                coverArt = null,
                                coverArtFilePath = null,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}
