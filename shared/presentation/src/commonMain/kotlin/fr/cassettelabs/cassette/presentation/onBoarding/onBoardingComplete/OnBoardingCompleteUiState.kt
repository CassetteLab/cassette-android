package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete

import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class OnBoardingCompleteUiState(
    val isLoading: Boolean = false,
) : UiState
