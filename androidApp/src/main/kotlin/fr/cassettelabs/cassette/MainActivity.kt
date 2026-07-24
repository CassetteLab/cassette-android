package fr.cassettelabs.cassette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val hasValidServerConfiguration = hasValidServerConfiguration(applicationContext)

            setContent {
                App(hasValidServerConfiguration = hasValidServerConfiguration)
            }

            keepSplash = false
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(hasValidServerConfiguration = false)
}
