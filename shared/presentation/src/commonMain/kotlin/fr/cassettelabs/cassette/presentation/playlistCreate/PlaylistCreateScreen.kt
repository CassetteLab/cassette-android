package fr.cassettelabs.cassette.presentation.playlistCreate

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_create_name_label
import cassette.shared.presentation.generated.resources.playlist_create_title
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
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
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistCreateScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: PlaylistCreateUiState,
    onEvent: (PlaylistCreateEvent) -> Unit,
) {
    LaunchedEffect(uiState.isCreated) {
        if (uiState.isCreated) {
            onEvent(PlaylistCreateEvent.OnBackClicked)
        }
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
                    text = "Crééer la playlist"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding.plus(contentPadding))
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onEvent(PlaylistCreateEvent.OnNameChanged(it)) },
                label = { Text(stringResource(Res.string.playlist_create_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun PlaylistCreateScreenPreview() {
    CassetteTheme {
        PlaylistCreateScreen(
            uiState = PlaylistCreateUiState(name = "Ma playlist"),
            onEvent = {}
        )
    }
}
