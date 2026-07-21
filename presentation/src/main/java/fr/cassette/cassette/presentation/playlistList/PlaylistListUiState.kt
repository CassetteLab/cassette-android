package fr.cassette.cassette.presentation.playlistList

import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class PlaylistListUiState(
    val isLoading: Boolean = true,
    val playlists: List<PlaylistList> = emptyList(),
    val hasError: Boolean = false,
) : UiState
