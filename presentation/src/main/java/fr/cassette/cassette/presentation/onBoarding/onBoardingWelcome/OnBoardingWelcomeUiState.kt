package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

internal data class OnBoardingWelcomeUiState(
    val hasAppeared: Boolean = false,
    val isGetStartedEnabled: Boolean = true,
    val isServerFormVisible: Boolean = false,
)
