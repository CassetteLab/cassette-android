package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel

internal class OnBoardingCacheViewModel(
    logger: Logger,
) : BaseViewModel<OnBoardingCacheUiState, OnBoardingCacheEvent>(
        viewModelName = "OnBoardingCacheViewModel",
        logger = logger,
        initialState = OnBoardingCacheUiState(),
    ) {
    override fun handleEvent(event: OnBoardingCacheEvent) {
        when (event) {
            OnBoardingCacheEvent.OnAppearing -> Unit
        }
    }
}
