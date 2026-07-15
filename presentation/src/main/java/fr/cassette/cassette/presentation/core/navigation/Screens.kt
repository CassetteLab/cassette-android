package fr.cassette.cassette.presentation.core.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Screens {

    @Serializable
    sealed interface OnBoardingScreens : Screens {
        @Serializable
        data object OnBoardingScreensWelcomeScreen: OnBoardingScreens

        @Serializable
        data object OnBoardingScreensServerConfigurationScreen: OnBoardingScreens

        @Serializable
        data object OnBoardingScreensCompleteScreen: OnBoardingScreens
    }

    @Serializable
    data object Home: Screens
}