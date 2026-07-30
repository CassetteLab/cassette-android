package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel

internal class OnBoardingWelcomeViewModel(
    logger: Logger,
) : BaseViewModel<OnBoardingWelcomeUiState, OnBoardingWelcomeEvent>(
        viewModelName = "OnBoardingWelcomeViewModel",
        logger = logger,
        initialState = OnBoardingWelcomeUiState(),
    ) {
    override fun handleEvent(event: OnBoardingWelcomeEvent) {
        when (event) {
            OnBoardingWelcomeEvent.OnAppearing -> logger.i("OnBoardingWelcomeScreen appeared")
            OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                logger.i("User clicked on get started")
                updateState { it.copy(isLoading = true) }
            }
        }
    }
}
