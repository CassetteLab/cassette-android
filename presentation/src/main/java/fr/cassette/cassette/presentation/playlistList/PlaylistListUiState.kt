package fr.cassette.cassette.presentation.playlistList

import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class PlaylistListUiState(
    val isLoading: Boolean = true,
    val isPullToRefreshIndicatorVisible: Boolean = false,
    val isRefreshing: Boolean = false,
    val playlists: List<PlaylistList> = emptyList(),
) : UiState
