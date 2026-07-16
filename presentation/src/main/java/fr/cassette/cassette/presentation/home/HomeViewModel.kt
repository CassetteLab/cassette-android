package fr.cassette.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtRequestUseCase
import fr.cassette.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getRecentlyAddedAlbumsUseCase: GetRecentlyAddedAlbumsUseCase,
    private val getAlbumCoverArtRequestUseCase: GetAlbumCoverArtRequestUseCase,
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
                val coverArtRequests = albums.associateNotNull { album ->
                    val coverArtId = album.coverArt ?: album.id
                    runCatching {
                        album.id to getAlbumCoverArtRequestUseCase(coverArtId = coverArtId, size = COVER_ART_SIZE)
                    }.getOrNull()
                }
                updateState { it.copy(isLoading = false, albums = albums, albumCoverArtRequests = coverArtRequests) }
            } catch (exception: Exception) {
                logger.w("Unable to load recently added albums", exception)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private inline fun <T, K, V> Iterable<T>.associateNotNull(transform: (T) -> Pair<K, V>?): Map<K, V> {
        return mapNotNull(transform).toMap()
    }

    private companion object {
        const val RECENT_ALBUMS_SIZE = 20
        const val COVER_ART_SIZE = 240
    }
}
