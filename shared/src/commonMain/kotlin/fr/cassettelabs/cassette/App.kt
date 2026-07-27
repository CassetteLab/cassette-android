package fr.cassettelabs.cassette

import androidx.compose.runtime.Composable
import fr.cassettelabs.cassette.presentation.core.navigation.CassetteNavigation
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
fun App(hasValidServerConfiguration: Boolean) {
    CassetteTheme {
        CassetteNavigation(
            startDestination =
                if (hasValidServerConfiguration) {
                    Screens.Main
                } else {
                    Screens.OnBoardingScreens.OnBoardingScreensWelcomeScreen
                },
        )
    }
}
