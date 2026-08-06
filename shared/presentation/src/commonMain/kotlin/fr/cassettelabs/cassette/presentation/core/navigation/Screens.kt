package fr.cassettelabs.cassette.presentation.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens {
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
    data object SettingsApplicationInformation : Screens

    @Serializable
    data object SettingsConfiguration : Screens

    @Serializable
    data object SettingsServerConfiguration : Screens

    @Serializable
    data object AlbumList : Screens

    @Serializable
    data object ArtistList : Screens

    @Serializable
    data object TrackList : Screens

    @Serializable
    data object PlaylistList : Screens

    @Serializable
    data object Starred : Screens

    @Serializable
    data object Downloads : Screens

    @Serializable
    data class AlbumDetail(
        val albumId: String,
    ) : Screens

    @Serializable
    data class ArtistDetail(
        val artistId: String,
    ) : Screens

    @Serializable
    data class PlaylistDetail(
        val playlistId: String,
    ) : Screens

    @Serializable
    data object NowPlaying : Screens

    @Serializable
    data object PlaybackQueue : Screens
}
