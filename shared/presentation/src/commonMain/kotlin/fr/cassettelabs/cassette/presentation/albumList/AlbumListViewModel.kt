package fr.cassettelabs.cassette.presentation.albumList

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class AlbumListViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
    logger: Logger,
) : BaseViewModel<AlbumListUiState, AlbumListEvent>(
        viewModelName = "AlbumListViewModel",
        logger = logger,
        initialState = AlbumListUiState(),
    ) {
    override fun handleEvent(event: AlbumListEvent) {
        when (event) {
            AlbumListEvent.OnAppearing -> {
                viewModelScope.launch {
                    getAllAlbumsUseCase()
                        .onStart { updateState { it.copy(isLoading = true) } }
                        .collect { albums ->
                            updateState { it.copy(isLoading = false, albums = albums) }
                        }
                }
                viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true) }
                    refreshAlbums()
                }
                    .invokeOnCompletion {
                        updateState { it.copy(isRefreshing = false) }
                    }

            }
            is AlbumListEvent.OnAlbumClicked -> Unit
            AlbumListEvent.OnRefresh -> {
                viewModelScope.launch {
                    updateState { it.copy(isRefreshing = true, isPullToRefreshIndicatorVisible = true) }
                    refreshAlbums()
                }
                    .invokeOnCompletion {
                        updateState { it.copy(isRefreshing = false, isPullToRefreshIndicatorVisible = false) }
                    }
            }
        }
    }

    private suspend fun refreshAlbums() {
        try {
            refreshAlbumsUseCase()
        } catch (exception: Exception) {
            logger.w("Unable to refresh albums" + ": " + exception.message)
        }
    }
}
