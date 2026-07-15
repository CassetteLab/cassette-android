package fr.cassette.cassette.presentation.core.mvi

import androidx.lifecycle.ViewModel
import fr.cassette.cassette.core.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal abstract class BaseViewModel<S : UiState, E : Event>(
    private val viewModelName: String,
    initialState: S,
    protected val logger: Logger,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    init {
        logger.init(viewModelName)
    }

    fun onEvent(event: E) {
        logger.d("$viewModelName received new event: $event")
        handleEvent(event)
    }

    protected abstract fun handleEvent(event: E)

    protected fun updateState(transform: (S) -> S) {
        _uiState.update(transform)
    }
}
