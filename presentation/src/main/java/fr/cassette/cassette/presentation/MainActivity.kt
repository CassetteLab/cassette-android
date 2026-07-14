package fr.cassette.cassette.presentation

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationScreen
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeEvent
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
                val onBoardingWelcomeViewModel = koinViewModel<OnBoardingWelcomeViewModel>()
                val onBoardingWelcomeUiState by onBoardingWelcomeViewModel.uiState.collectAsStateWithLifecycle()
                val serverConfigurationViewModel = koinViewModel<ServerConfigurationViewModel>()
                val serverConfigurationUiState by serverConfigurationViewModel.uiState.collectAsStateWithLifecycle()

                if (onBoardingWelcomeUiState.isServerConfigurationVisible) {
                    BackHandler {
                        onBoardingWelcomeViewModel.onEvent(OnBoardingWelcomeEvent.OnServerConfigurationBackClicked)
                    }
                    ServerConfigurationScreen(
                        uiState = serverConfigurationUiState,
                        onEvent = serverConfigurationViewModel::onEvent,
                    )
                } else {
                    OnBoardingWelcomeScreen(
                        uiState = onBoardingWelcomeUiState,
                        onEvent = onBoardingWelcomeViewModel::onEvent,
                    )
                }
            }
        }
    }
}
