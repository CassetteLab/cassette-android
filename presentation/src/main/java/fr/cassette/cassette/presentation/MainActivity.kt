package fr.cassette.cassette.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheScreen
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassette.cassette.presentation.ui.theme.CassetteTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CassetteTheme {
                val viewModel = koinViewModel<OnBoardingCacheViewModel>()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                OnBoardingCacheScreen(uiState = uiState, onEvent = viewModel::onEvent)
            }
        }
    }
}