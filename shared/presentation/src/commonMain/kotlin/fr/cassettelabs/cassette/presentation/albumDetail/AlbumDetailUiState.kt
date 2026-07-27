package fr.cassettelabs.cassette.presentation.albumDetail

import fr.cassettelabs.cassette.domain.aliases.AlbumId
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val album: Album? = null,
    val tracks: List<Track> = emptyList(),
    val currentTrackId: String? = null,
    val isPlaying: Boolean = false,
    val coverArt: AlbumCoverArt? = null,

    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,

    val isPullToRefreshIndicatorVisible: Boolean = false,
    val isRefreshing: Boolean = false,
) : UiState
