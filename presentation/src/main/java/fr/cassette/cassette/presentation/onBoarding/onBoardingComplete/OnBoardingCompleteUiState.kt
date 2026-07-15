package fr.cassette.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassette.cassette.presentation.core.mvi.UiState

internal data class OnBoardingCompleteUiState(
    val isLoading: Boolean = false,
) : UiState
