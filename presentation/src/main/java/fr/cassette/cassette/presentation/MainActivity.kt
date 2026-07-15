package fr.cassette.cassette.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import fr.cassette.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassette.cassette.presentation.navigation.CassetteNavigation
import fr.cassette.cassette.presentation.navigation.CassetteStartDestination
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val hasServerConfigurationUseCase: HasServerConfigurationUseCase by inject()
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val startDestination = if (hasServerConfigurationUseCase()) {
                CassetteStartDestination.Home
            } else {
                CassetteStartDestination.OnBoardingWelcome
            }

            setContent {
                CassetteTheme {
                    CassetteNavigation(startDestination = startDestination)
                }
            }

            keepSplash = false
        }
    }
}
