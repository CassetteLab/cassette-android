package fr.cassette.cassette.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import fr.cassette.cassette.presentation.ui.theme.CassetteTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CassetteTheme {
                val viewModel = koinViewModel<OnBoardingWelcomeViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                OnBoardingWelcomeScreen(uiState = uiState, onEvent = viewModel::onEvent)
            }
        }
    }
}
