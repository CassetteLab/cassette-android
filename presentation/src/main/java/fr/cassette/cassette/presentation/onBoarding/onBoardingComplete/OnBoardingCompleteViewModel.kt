package fr.cassette.cassette.presentation.onBoarding.onBoardingComplete

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class OnBoardingCompleteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingCompleteUiState())
    val uiState: StateFlow<OnBoardingCompleteUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnBoardingCompleteEvent) {
        when (event) {
            OnBoardingCompleteEvent.OnStartListeningClicked -> {

            }
        }
    }
}
