package fr.cassette.cassette.presentation.main

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class MainViewModel(
    logger: Logger,
) : BaseViewModel<MainUiState, MainEvent>(
    viewModelName = "MainViewModel",
    logger = logger,
    initialState = MainUiState(),
) {
    override fun handleEvent(event: MainEvent) = Unit
}
