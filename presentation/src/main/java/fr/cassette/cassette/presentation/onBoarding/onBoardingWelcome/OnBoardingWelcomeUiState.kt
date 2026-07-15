package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class OnBoardingWelcomeUiState(
    val isLoading: Boolean = false,
) : UiState
