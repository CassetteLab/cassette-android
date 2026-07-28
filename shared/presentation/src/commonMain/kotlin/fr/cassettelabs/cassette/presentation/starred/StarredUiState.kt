package fr.cassettelabs.cassette.presentation.starred

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class StarredUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isPullToRefreshIndicatorVisible: Boolean = false,
    val albums: List<Album> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val albumCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
    val trackCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
) : UiState {
    val isEmpty: Boolean = !isLoading && albums.isEmpty() && tracks.isEmpty()
}
