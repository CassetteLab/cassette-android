package fr.cassettelabs.cassette.presentation.home

import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val albums: List<AlbumList> = emptyList(),
) : UiState
