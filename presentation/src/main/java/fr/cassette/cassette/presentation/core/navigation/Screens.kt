package fr.cassette.cassette.presentation.core.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Screens {

    @Serializable
    sealed interface OnBoardingScreens : Screens {
        @Serializable
        data object OnBoardingScreensWelcomeScreen : OnBoardingScreens

        @Serializable
        data object OnBoardingScreensServerConfigurationScreen : OnBoardingScreens

        @Serializable
        data object OnBoardingScreensCompleteScreen : OnBoardingScreens
    }

    @Serializable
    data object Main : Screens

    @Serializable
    data object Home : Screens

    @Serializable
    data object Settings : Screens

    @Serializable
    data object SettingsServerConfiguration : Screens

    @Serializable
    data class AlbumDetail(val albumId: String) : Screens
}
