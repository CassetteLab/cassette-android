package fr.cassette.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class OnBoardingCompleteViewModel(
    logger: Logger,
) : BaseViewModel<OnBoardingCompleteUiState, OnBoardingCompleteEvent>(
    viewModelName = "OnBoardingCompleteViewModel",
    logger = logger,
    initialState = OnBoardingCompleteUiState(),
) {

    override fun handleEvent(event: OnBoardingCompleteEvent) {
        when (event) {
            OnBoardingCompleteEvent.OnStartListeningClicked -> {
                updateState { it.copy(isLoading = true) }
            }
        }
    }
}
