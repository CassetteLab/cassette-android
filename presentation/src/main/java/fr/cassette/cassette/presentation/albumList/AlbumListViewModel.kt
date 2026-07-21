package fr.cassette.cassette.presentation.albumList

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassette.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
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
    init {
        viewModelScope.launch {
            getAllAlbumsUseCase()
                .onStart { updateState { it.copy(isLoading = true, hasError = false) } }
                .collect { albums ->
                    updateState { it.copy(isLoading = false, albums = albums) }
                }
        }
    }

    override fun handleEvent(event: AlbumListEvent) {
        when (event) {
            AlbumListEvent.OnAppearing -> refreshAlbums()
            is AlbumListEvent.OnAlbumClicked -> Unit
            AlbumListEvent.OnRefresh -> refreshAlbums()
            AlbumListEvent.OnRetryClicked -> refreshAlbums()
        }
    }

    private fun refreshAlbums() {
        viewModelScope.launch {
            updateState { it.copy(isRefreshing = true, hasError = false) }
            try {
                refreshAlbumsUseCase()
                updateState { it.copy(isRefreshing = false) }
            } catch (exception: Exception) {
                logger.w("Unable to refresh albums", exception)
                val currentAlbums = uiState.value.albums
                updateState { it.copy(isRefreshing = false, hasError = currentAlbums.isEmpty()) }
            }
        }
    }
}
