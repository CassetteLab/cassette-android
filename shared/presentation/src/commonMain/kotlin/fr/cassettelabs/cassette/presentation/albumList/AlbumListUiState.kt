package fr.cassettelabs.cassette.presentation.albumList

import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class AlbumListUiState(
    val isLoading: Boolean = true,
    val isPullToRefreshIndicatorVisible : Boolean = false,
    val isRefreshing: Boolean = false,
    val albums: List<AlbumList> = emptyList(),
) : UiState
