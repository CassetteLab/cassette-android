package fr.cassette.cassette.presentation.albumDetail

import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val albumId: String,
    val album: AlbumDetail? = null,
    val tracks: List<Track> = emptyList(),
    val coverArt: AlbumCoverArt? = null,
    val isLoading: Boolean = false,
    val isTracksLoading: Boolean = false,
) : UiState
