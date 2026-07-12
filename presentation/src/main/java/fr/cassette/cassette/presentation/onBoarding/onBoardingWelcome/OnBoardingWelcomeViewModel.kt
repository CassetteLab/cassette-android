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
            OnBoardingWelcomeEvent.OnAppearing -> _uiState.update { uiState ->
                uiState.copy(hasAppeared = true)
            }

            OnBoardingWelcomeEvent.OnGetStartedClicked -> _uiState.update { uiState ->
                uiState.copy(isServerFormVisible = true)
            }

            OnBoardingWelcomeEvent.OnServerFormDismissed -> _uiState.update { uiState ->
                uiState.copy(isServerFormVisible = false)
            }
        }
    }
}
