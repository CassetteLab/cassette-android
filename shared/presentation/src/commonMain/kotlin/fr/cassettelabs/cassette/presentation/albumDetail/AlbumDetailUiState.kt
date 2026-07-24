package fr.cassettelabs.cassette.presentation.albumDetail

import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumDetail
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val albumId: String,
    val album: AlbumDetail? = null,
    val tracks: List<Track> = emptyList(),
    val coverArt: AlbumCoverArt? = null,
    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,
) : UiState
