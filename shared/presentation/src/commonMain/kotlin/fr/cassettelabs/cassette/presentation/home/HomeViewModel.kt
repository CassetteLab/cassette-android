package fr.cassettelabs.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
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
                val albumCoverArts =
                    albums
                        .mapNotNull { album ->
                            album.coverArtFilePath?.let { filePath -> album.id to AlbumCoverArt(filePath = filePath) }
                        }.toMap()
                updateState { it.copy(isLoading = false, albums = albums, albumCoverArts = albumCoverArts) }

                albums.forEach { album ->
                    if (album.coverArtFilePath != null) return@forEach

                    val coverArtId = album.coverArt ?: album.id
                    runCatching {
                        getAlbumCoverArtUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE, albumId = album.id)
                    }.onSuccess { coverArt ->
                        updateState { state ->
                            state.copy(albumCoverArts = state.albumCoverArts + (album.id to coverArt))
                        }
                    }.onFailure { exception ->
                        logger.w("Unable to load cover art for album ${album.id}" + ": " + exception.message)
                    }
                }
            } catch (exception: Exception) {
                logger.w("Unable to load recently added albums" + ": " + exception.message)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private companion object {
        const val RECENT_ALBUMS_SIZE = 20
        const val COVER_ART_SIZE = 240
    }
}
