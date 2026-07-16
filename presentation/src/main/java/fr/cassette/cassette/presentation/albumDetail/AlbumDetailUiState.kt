package fr.cassette.cassette.presentation.albumDetail

import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class AlbumDetailUiState(
    val albumId: String,
    val album: AlbumDetail? = null,
    val coverArtRequest: AlbumCoverArtRequest? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
) : UiState
