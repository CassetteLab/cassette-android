package fr.cassettelabs.cassette.presentation.playlistDetail

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaylistDetailUiState(
    val playlistId: String,
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val coverArtStatus: CoverArtLoadingStatus? = null,
    val trackCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
    val trackStarredStatuses: Map<String, Boolean> = emptyMap(),
    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,
    val isDeleting: Boolean = false,
) : UiState
