package fr.cassettelabs.cassette.presentation.albumDetail

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val albumId: String = "",
    val album: Album? = null,
    val tracks: List<Track> = emptyList(),
    val currentTrackId: String? = null,
    val isPlaying: Boolean = false,
    val coverArtStatus: CoverArtLoadingStatus? = null,
    val albumStarred: Boolean = false,
    val trackStarredStatuses: Map<String, Boolean> = emptyMap(),

    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,

    val isPullToRefreshIndicatorVisible: Boolean = false,
    val isRefreshing: Boolean = false,
) : UiState
