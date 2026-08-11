package fr.cassettelabs.cassette

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import fr.cassettelabs.cassette.core.helpers.SettingsHelper
import fr.cassettelabs.cassette.presentation.core.navigation.CassetteNavigation
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.koin.compose.koinInject

@Composable
fun App(hasValidServerConfiguration: Boolean) {
    val settingsHelper: SettingsHelper = koinInject()
    val themeMode by settingsHelper.themeFlow.collectAsState()

    CassetteTheme(themeMode = themeMode) {
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
