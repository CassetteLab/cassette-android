package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class OnBoardingWelcomeUiState(
    val isLoading: Boolean = false,
) : UiState
