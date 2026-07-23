package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class OnBoardingWelcomeViewModel(
    logger: Logger,
) : BaseViewModel<OnBoardingWelcomeUiState, OnBoardingWelcomeEvent>(
        viewModelName = "OnBoardingWelcomeViewModel",
        logger = logger,
        initialState = OnBoardingWelcomeUiState(),
    ) {
    override fun handleEvent(event: OnBoardingWelcomeEvent) {
        when (event) {
            OnBoardingWelcomeEvent.OnAppearing -> {
                logger.i("OnBoardingWelcomeScreen appeared")
            }
            OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                logger.i("User clicked on get started")
                updateState { it.copy(isLoading = true) }
            }
        }
    }
}
