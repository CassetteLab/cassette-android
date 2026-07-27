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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.CurrentTrack
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
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
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
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
        if (uiState.upcomingTracks.isEmpty()) {
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
                        PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(
                    items = uiState.upcomingTracks,
                    key = { _, currentTrack -> currentTrack.track.id },
                ) { index, currentTrack ->
                    PlaybackQueueTrackRow(
                        position = index + 1,
                        currentTrack = currentTrack,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun PlaybackQueueScreenPreview() {
    CassetteTheme {
        PlaybackQueueScreen(
            uiState =
                PlaybackQueueUiState(
                    upcomingTracks =
                        listOf(
                            CurrentTrack(
                                track = Track(id = "track-1", title = "Aerodynamic", artist = "Daft Punk", trackNumber = 2, durationSeconds = 212),
                                albumId = "album-1",
                                albumName = "Discovery",
                                coverArtId = null,
                                coverArtFilePath = null,
                            ),
                            CurrentTrack(
                                track = Track(id = "track-2", title = "Digital Love", artist = "Daft Punk", trackNumber = 3, durationSeconds = 301),
                                albumId = "album-1",
                                albumName = "Discovery",
                                coverArtId = null,
                                coverArtFilePath = null,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}
