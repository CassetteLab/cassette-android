package fr.cassettelabs.cassette

import androidx.compose.runtime.Composable
import fr.cassettelabs.cassette.di.platformModule
import fr.cassettelabs.cassette.di.sharedModules
import fr.cassettelabs.cassette.presentation.core.navigation.CassetteNavigation
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.koin.compose.KoinApplication

@Composable
fun App(hasValidServerConfiguration: Boolean) {
    val platformModule = platformModule()
    KoinApplication(
        application = {
            modules(sharedModules() + platformModule)
        },
    ) {
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
}
