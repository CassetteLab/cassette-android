package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel

internal class OnBoardingCompleteViewModel(
    logger: Logger,
) : BaseViewModel<OnBoardingCompleteUiState, OnBoardingCompleteEvent>(
        viewModelName = "OnBoardingCompleteViewModel",
        logger = logger,
        initialState = OnBoardingCompleteUiState(),
    ) {
    override fun handleEvent(event: OnBoardingCompleteEvent) {
        when (event) {
            OnBoardingCompleteEvent.OnStartListeningClicked -> updateState { it.copy(isLoading = true) }
        }
    }
}
