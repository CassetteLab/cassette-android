package fr.cassette.cassette.presentation.albumDetail

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetAlbumUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class AlbumDetailViewModel(
    private val albumId: String,
    private val getAlbumUseCase: GetAlbumUseCase,
    logger: Logger,
) : BaseViewModel<AlbumDetailUiState, AlbumDetailEvent>(
    viewModelName = "AlbumDetailViewModel",
    logger = logger,
    initialState = AlbumDetailUiState(albumId = albumId),
) {
    init {
        loadAlbum()
    }

    override fun handleEvent(event: AlbumDetailEvent) {
        when (event) {
            AlbumDetailEvent.OnBackClicked -> Unit
            AlbumDetailEvent.OnRetryClicked -> loadAlbum()
        }
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, hasError = false) }
            try {
                val album = getAlbumUseCase(albumId)
                updateState { it.copy(isLoading = false, album = album) }
            } catch (exception: Exception) {
                logger.w("Unable to load album $albumId", exception)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }
}
