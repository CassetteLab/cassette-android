package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class OnBoardingCacheViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingCacheUiState())
    val uiState: StateFlow<OnBoardingCacheUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnBoardingCacheEvent){
        when(event){
            OnBoardingCacheEvent.OnAppearing -> Unit
        }
    }
}