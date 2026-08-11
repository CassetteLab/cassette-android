package fr.cassettelabs.cassette.presentation.playlistCreate

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_create_button_label
import cassette.shared.presentation.generated.resources.playlist_create_name_label
import cassette.shared.presentation.generated.resources.playlist_create_title
import cassette.shared.presentation.generated.resources.playlist_create_tracks_empty_description
import cassette.shared.presentation.generated.resources.playlist_create_tracks_empty_title
import cassette.shared.presentation.generated.resources.playlist_create_tracks_loading
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage
import fr.cassettelabs.cassette.presentation.playlistCreate.core.PlaylistCreateTrackItem
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistCreateScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaylistCreateUiState,
    onEvent: (PlaylistCreateEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(PlaylistCreateEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier.padding(contentPadding).imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.playlist_create_title,
                onBackClicked = {
                    onEvent(PlaylistCreateEvent.OnBackClicked)
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = uiState.canCreate,
                    isLoading = uiState.isLoading,
                    onClick = { onEvent(PlaylistCreateEvent.OnCreateClicked) },
                    text = if (uiState.name.isNotBlank()) {
                        stringResource(Res.string.playlist_create_button_label) + " \"" + uiState.name + "\""
                    } else {
                        stringResource(Res.string.playlist_create_button_label)
                    },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onEvent(PlaylistCreateEvent.OnNameChanged(it)) },
                label = { Text(stringResource(Res.string.playlist_create_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoadingTracks -> {
                    LoadingMessage(message = Res.string.playlist_create_tracks_loading)
                }
                uiState.tracks.isEmpty() -> {
                    HomeMessage(
                        title = stringResource(Res.string.playlist_create_tracks_empty_title),
                        description = stringResource(Res.string.playlist_create_tracks_empty_description),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(
                            items = uiState.tracks,
                            key = { it.id },
                        ) { track ->
                            PlaylistCreateTrackItem(
                                track = track,
                                isSelected = track.id in uiState.selectedTrackIds,
                                onToggle = { onEvent(PlaylistCreateEvent.OnTrackToggled(track.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PlaylistCreateScreenPreview() {
    CassetteTheme {
        PlaylistCreateScreen(
            uiState =
                PlaylistCreateUiState(
                    name = "Ma playlist",
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
                            Track(
                                id = "track-3",
                                title = "Digital Love",
                                artist = "Daft Punk",
                                trackNumber = 3,
                                durationSeconds = 301,
                            ),
                        ),
                    selectedTrackIds = setOf("track-1"),
                ),
            onEvent = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun PlaylistCreateScreenEmptyPreview() {
    CassetteTheme {
        PlaylistCreateScreen(
            uiState = PlaylistCreateUiState(),
            onEvent = {},
        )
    }
}
