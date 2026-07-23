package fr.cassette.cassette.presentation.albumList

import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class AlbumListUiState(
    val isLoading: Boolean = true,
    val isPullToRefreshIndicatorVisible : Boolean = false,
    val isRefreshing: Boolean = false,
    val albums: List<AlbumList> = emptyList(),
) : UiState
