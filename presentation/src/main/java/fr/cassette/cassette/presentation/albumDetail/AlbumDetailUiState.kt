package fr.cassette.cassette.presentation.albumDetail

import fr.cassette.cassette.domain.models.Album
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val albumId: String,
    val album: Album? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
) : UiState
