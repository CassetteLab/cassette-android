package fr.cassette.cassette.presentation.home

import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val albums: List<AlbumList> = emptyList(),
    val albumCoverArtRequests: Map<String, AlbumCoverArtRequest> = emptyMap(),
    val hasError: Boolean = false,
) : UiState
