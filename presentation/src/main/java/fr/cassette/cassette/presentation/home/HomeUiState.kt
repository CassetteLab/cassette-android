package fr.cassette.cassette.presentation.home

import fr.cassette.cassette.domain.models.Album
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val hasError: Boolean = false,
) : UiState
