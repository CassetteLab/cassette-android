package fr.cassettelabs.cassette.presentation.playlistList

import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaylistListUiState(
    val isLoading: Boolean = true,
    val isPullToRefreshIndicatorVisible: Boolean = false,
    val isRefreshing: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
) : UiState
