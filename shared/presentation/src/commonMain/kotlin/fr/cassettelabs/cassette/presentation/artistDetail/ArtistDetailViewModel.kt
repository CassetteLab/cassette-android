package fr.cassettelabs.cassette.presentation.artistDetail

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.GetArtistAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.GetArtistUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.RefreshArtistUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class ArtistDetailViewModel(
    private val artistId: String,
    private val getArtistUseCase: GetArtistUseCase,
    private val getArtistAlbumsUseCase: GetArtistAlbumsUseCase,
    private val refreshArtistUseCase: RefreshArtistUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<ArtistDetailUiState, ArtistDetailEvent>(
        viewModelName = "ArtistDetailViewModel",
        logger = logger,
        initialState = ArtistDetailUiState(),
    ) {
    override fun handleEvent(event: ArtistDetailEvent) {
        when (event) {
            ArtistDetailEvent.OnAppearing -> {
                loadArtist()
                loadAlbums()
            }
            ArtistDetailEvent.OnBackClicked -> Unit
            is ArtistDetailEvent.OnAlbumClicked -> Unit
            ArtistDetailEvent.OnRefresh -> refreshArtistDetail()
        }
    }

    private fun loadArtist() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val artist = getArtistUseCase(artistId)
                updateState { it.copy(isLoading = false, artist = artist) }
                artist?.let { loadCoverArt(it) }
            } catch (exception: Exception) {
                logger.w("Unable to load artist $artistId" + ": " + exception.message)
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            updateState { it.copy(isAlbumsLoading = true) }
            try {
                val albums = getArtistAlbumsUseCase(artistId)
                updateState { it.copy(isAlbumsLoading = false, albums = albums) }
            } catch (exception: Exception) {
                logger.w("Unable to load artist albums $artistId" + ": " + exception.message)
                updateState { it.copy(isAlbumsLoading = false) }
            }
        }
    }

    private fun refreshArtistDetail() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }
            try {
                val artist = refreshArtistUseCase(artistId)
                updateState { it.copy(artist = artist) }
                loadCoverArt(artist)
            } catch (exception: Exception) {
                logger.w("Unable to refresh artist $artistId" + ": " + exception.message)
            }

            try {
                val albums = getArtistAlbumsUseCase(artistId)
                updateState { it.copy(albums = albums) }
            } catch (exception: Exception) {
                logger.w("Unable to refresh artist albums $artistId" + ": " + exception.message)
            }
        }.invokeOnCompletion {
            updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
        }
    }

    private fun loadCoverArt(artist: Artist) {
        val coverArtId = artist.coverArt ?: return
        getAlbumCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE)
            .onEach { status ->
                if (status is CoverArtLoadingStatus.Error) {
                    logger.w("Unable to load cover art for artist ${artist.id}" + ": " + status.throwable.message)
                }
                updateState { it.copy(coverArtStatus = status) }
            }.launchIn(viewModelScope)
    }

    private companion object {
        const val COVER_ART_SIZE = 900
    }
}
