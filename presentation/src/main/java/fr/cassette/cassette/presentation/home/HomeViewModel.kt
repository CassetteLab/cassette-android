package fr.cassette.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getRecentlyAddedAlbumsUseCase: GetRecentlyAddedAlbumsUseCase,
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
            HomeEvent.OnRetryClicked -> loadRecentlyAddedAlbums()
        }
    }

    private fun loadRecentlyAddedAlbums() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, hasError = false) }
            try {
                val albums = getRecentlyAddedAlbumsUseCase(size = RECENT_ALBUMS_SIZE)
                updateState { it.copy(isLoading = false, albums = albums) }
            } catch (exception: Exception) {
                logger.w("Unable to load recently added albums", exception)
                updateState { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private companion object {
        const val RECENT_ALBUMS_SIZE = 20
    }
}
