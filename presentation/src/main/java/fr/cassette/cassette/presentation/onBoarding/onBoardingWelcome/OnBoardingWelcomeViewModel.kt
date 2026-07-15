package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import androidx.lifecycle.ViewModel
import fr.cassette.cassette.core.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class OnBoardingWelcomeViewModel(
    private val logger: Logger
) : ViewModel() {

    init {
        logger.init("OnBoardingWelcomeViewModel")
    }

    private val _uiState = MutableStateFlow(OnBoardingWelcomeUiState())
    val uiState: StateFlow<OnBoardingWelcomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnBoardingWelcomeEvent) {
        when (event) {
            OnBoardingWelcomeEvent.OnAppearing -> {
                logger.i("OnBoardingWelcomeScreen appeared")
            }
            OnBoardingWelcomeEvent.OnGetStartedClicked -> {
                logger.i("User clicked on get started")
                _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}
