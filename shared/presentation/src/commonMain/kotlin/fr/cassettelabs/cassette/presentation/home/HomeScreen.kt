package fr.cassettelabs.cassette.presentation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import fr.cassettelabs.cassette.presentation.albumList.AlbumListScreen

@Composable
internal fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    AlbumListScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onEvent = onEvent,
    )
}
