package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val albumCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
    val hasError: Boolean = false,
) : UiState
