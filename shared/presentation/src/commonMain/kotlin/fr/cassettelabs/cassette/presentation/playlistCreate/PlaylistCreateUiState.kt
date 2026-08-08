package fr.cassettelabs.cassette.presentation.playlistCreate

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaylistCreateUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
) : UiState {
    val canCreate: Boolean get() = name.isNotBlank()
}
