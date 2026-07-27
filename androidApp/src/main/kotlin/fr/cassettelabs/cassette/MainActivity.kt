package fr.cassettelabs.cassette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import fr.cassettelabs.cassette.domain.usecases.HasServerConfigurationUseCase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private var keepSplash = true
    private val hasServerConfigurationUseCase : HasServerConfigurationUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val hasValidServerConfiguration = hasServerConfigurationUseCase()

            setContent {
                App(hasValidServerConfiguration = hasValidServerConfiguration)
            }

            keepSplash = false
        }
    }
}
