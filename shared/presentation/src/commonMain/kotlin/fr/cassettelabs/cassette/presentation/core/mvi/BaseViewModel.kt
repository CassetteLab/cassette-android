package fr.cassettelabs.cassette.presentation.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal abstract class BaseViewModel<S : UiState, E : Event>(
    private val viewModelName: String,
    initialState: S,
    protected val logger: Logger,
) : ViewModel() {
    protected val scope = viewModelScope
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    init {
        logger.init(viewModelName)
    }

    fun onEvent(event: E) {
        if (event is SensitiveEvent) {
            logger.d("$viewModelName received new sensitive event")
        } else {
            logger.d("$viewModelName received new event: $event")
        }
        handleEvent(event)
    }

    protected abstract fun handleEvent(event: E)

    protected fun updateState(transform: (S) -> S) {
        _uiState.update(transform)
    }
}
