package fr.cassettelabs.cassette.presentation.playlistDetail

import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.PlaylistDetail
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaylistDetailUiState(
    val playlistId: String,
    val playlist: PlaylistDetail? = null,
    val tracks: List<Track> = emptyList(),
    val coverArt: AlbumCoverArt? = null,
    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,
) : UiState
