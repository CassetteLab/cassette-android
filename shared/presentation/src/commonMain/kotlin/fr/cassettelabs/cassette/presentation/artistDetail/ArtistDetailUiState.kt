package fr.cassettelabs.cassette.presentation.artistDetail

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class ArtistDetailUiState(
    val artist: Artist? = null,
    val albums: List<Album> = emptyList(),
    val coverArtStatus: CoverArtLoadingStatus? = null,
    val isLoading: Boolean = false,
    val isAlbumsLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPullToRefreshIndicatorVisible: Boolean = false,
) : UiState
