package fr.cassettelabs.cassette.presentation.playlistCreate

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_create_back
import cassette.shared.presentation.generated.resources.playlist_create_title
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistCreateScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onEvent: (PlaylistCreateEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                navigationIcon = {
                    IconButton(onClick = { onEvent(PlaylistCreateEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.playlist_create_back),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.playlist_create_title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
            )
        },
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(innerPadding.plus(contentPadding)),
        )
    }
}

@Composable
@PreviewLightDark
private fun PlaylistCreateScreenPreview() {
    CassetteTheme {
        PlaylistCreateScreen(onEvent = {})
    }
}
