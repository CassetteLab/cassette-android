package fr.cassette.cassette.presentation.main

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class MainViewModel(
    getCurrentTrackUseCase: GetCurrentTrackUseCase,
    logger: Logger,
) : BaseViewModel<MainUiState, MainEvent>(
    viewModelName = "MainViewModel",
    logger = logger,
    initialState = MainUiState(),
) {
    init {
        getCurrentTrackUseCase()
            .onEach { track -> updateState { it.copy(currentTrack = track) } }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: MainEvent) = Unit
}
