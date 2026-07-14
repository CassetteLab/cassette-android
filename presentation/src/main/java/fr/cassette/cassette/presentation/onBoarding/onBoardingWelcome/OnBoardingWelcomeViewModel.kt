package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class OnBoardingWelcomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingWelcomeUiState())
    val uiState: StateFlow<OnBoardingWelcomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnBoardingWelcomeEvent) {
        when (event) {
            OnBoardingWelcomeEvent.OnAppearing -> {

            }
            OnBoardingWelcomeEvent.OnGetStartedClicked -> _uiState.update { uiState ->
                uiState.copy(isServerConfigurationVisible = true)
            }
            OnBoardingWelcomeEvent.OnServerConfigurationBackClicked -> _uiState.update { uiState ->
                uiState.copy(isServerConfigurationVisible = false)
            }
        }
    }
}
