package fr.cassettelabs.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getRecentlyAddedAlbumsUseCase: GetRecentlyAddedAlbumsUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<HomeUiState, HomeEvent>(
        viewModelName = "HomeViewModel",
        logger = logger,
        initialState = HomeUiState(),
    ) {
    init {
        loadRecentlyAddedAlbums()
    }

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAlbumClicked -> Unit
            HomeEvent.OnRetryClicked -> loadRecentlyAddedAlbums()
        }
    }

    private fun loadRecentlyAddedAlbums() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, hasError = false) }
            try {
                val albums = getRecentlyAddedAlbumsUseCase(size = RECENT_ALBUMS_SIZE)
                val albumCoverArtStatuses =
                    albums
                        .mapNotNull { album ->
                            album.coverArtFilePath?.let { filePath -> album.id to CoverArtLoadingStatus.Loaded(filePath) }
                        }.toMap()
                updateState { it.copy(isLoading = false, albums = albums, albumCoverArtStatuses = albumCoverArtStatuses) }

                albums.forEach { album ->
                    loadCoverArt(album)
                }
            } catch (exception: Exception) {
                logger.w("Unable to load recently added albums" + ": " + exception.message)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private fun loadCoverArt(album: Album) {
        getAlbumCoverArtUseCase(
            coverArtId = album.coverArt ?: album.id,
            size = COVER_ART_SIZE,
            albumId = album.id,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load cover art for album ${album.id}" + ": " + status.throwable.message)
            }
            updateState { state ->
                state.copy(albumCoverArtStatuses = state.albumCoverArtStatuses + (album.id to status))
            }
        }.launchIn(viewModelScope)
    }

    private companion object {
        const val RECENT_ALBUMS_SIZE = 20
        const val COVER_ART_SIZE = 240
    }
}
