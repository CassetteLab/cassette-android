package fr.cassettelabs.cassette.presentation.home

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal class HomeViewModel(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val refreshAlbumsUseCase: RefreshAlbumsUseCase,
    logger: Logger,
) : BaseViewModel<HomeUiState, HomeEvent>(
        viewModelName = "HomeViewModel",
        logger = logger,
        initialState = HomeUiState(),
    ) {
    private var hasAppeared = false

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnAppearing -> onAppearing()
            is HomeEvent.OnAlbumClicked -> Unit
            HomeEvent.OnRefresh -> refreshAlbums(showRefreshing = true)
        }
    }

    private fun onAppearing() {
        if (hasAppeared) return
        hasAppeared = true

        viewModelScope.launch {
            getAllAlbumsUseCase()
                .onStart { updateState { it.copy(isLoading = true) } }
                .collect { albums ->
                    updateState { it.copy(isLoading = false, albums = albums) }
                }
        }
        refreshAlbums(showRefreshing = false)
    }

    private fun refreshAlbums(showRefreshing: Boolean) {
        viewModelScope
            .launch {
                updateState { it.copy(isRefreshing = showRefreshing) }
                try {
                    refreshAlbumsUseCase()
                } catch (exception: Exception) {
                    logger.w("Unable to refresh albums: ${exception.message}")
                }
            }.invokeOnCompletion {
                updateState { it.copy(isRefreshing = false, isLoading = false) }
            }
    }
}
