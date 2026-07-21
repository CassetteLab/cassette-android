package fr.cassette.cassette.presentation.albumList

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class AlbumListViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    logger: Logger,
) : BaseViewModel<AlbumListUiState, AlbumListEvent>(
    viewModelName = "AlbumListViewModel",
    logger = logger,
    initialState = AlbumListUiState(),
) {
    override fun handleEvent(event: AlbumListEvent) {
        when (event) {
            AlbumListEvent.OnAppearing -> loadAlbums()
            is AlbumListEvent.OnAlbumClicked -> Unit
            AlbumListEvent.OnRetryClicked -> loadAlbums()
        }
    }

    private fun loadAlbums() {
        viewModelScope.launch {
            getAllAlbumsUseCase()
                .onStart { updateState { it.copy(isLoading = true, hasError = false) } }
                .catch { exception ->
                    logger.w("Unable to load albums", exception)
                    updateState { it.copy(isLoading = false, hasError = true) }
                }
                .collect { albums ->
                    updateState { it.copy(isLoading = false, albums = albums) }
                }
        }
    }
}
