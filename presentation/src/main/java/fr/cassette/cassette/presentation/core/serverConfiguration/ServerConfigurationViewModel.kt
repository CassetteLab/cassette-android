package fr.cassette.cassette.presentation.core.serverConfiguration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.models.ServerConfigurationCustomHeader
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ServerConfigurationViewModel(
    private val saveServerConfigurationUseCase: SaveServerConfigurationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerConfigurationUiState())
    val uiState: StateFlow<ServerConfigurationUiState> = _uiState.asStateFlow()

    private var nextHeaderId = 0L

    fun onEvent(event: ServerConfigurationEvent) {
        when (event) {
            is ServerConfigurationEvent.OnServerUrlChanged -> _uiState.update { uiState ->
                uiState.copy(serverUrl = event.value)
            }

            is ServerConfigurationEvent.OnUsernameChanged -> _uiState.update { uiState ->
                uiState.copy(username = event.value)
            }

            is ServerConfigurationEvent.OnPasswordChanged -> _uiState.update { uiState ->
                uiState.copy(password = event.value)
            }

            ServerConfigurationEvent.OnAddHeaderClicked -> _uiState.update { uiState ->
                uiState.copy(
                    customHeaders = uiState.customHeaders + ServerConfigurationHeaderUiState(id = nextHeaderId++),
                )
            }

            is ServerConfigurationEvent.OnRemoveHeaderClicked -> _uiState.update { uiState ->
                uiState.copy(customHeaders = uiState.customHeaders.filterNot { it.id == event.id })
            }

            is ServerConfigurationEvent.OnHeaderNameChanged -> updateHeader(event.id) { header ->
                header.copy(name = event.value)
            }

            is ServerConfigurationEvent.OnHeaderValueChanged -> updateHeader(event.id) { header ->
                header.copy(value = event.value)
            }

            is ServerConfigurationEvent.OnHeaderValueVisibilityChanged -> updateHeader(event.id) { header ->
                header.copy(isValueVisible = event.isVisible)
            }

            ServerConfigurationEvent.OnConnectClicked -> saveServerConfiguration()
        }
    }

    private fun saveServerConfiguration() {
        val uiState = _uiState.value
        if (!uiState.canSubmit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                saveServerConfigurationUseCase(
                    ServerConfiguration(
                        serverUrl = uiState.serverUrl,
                        username = uiState.username,
                        password = uiState.password,
                        customHeaders = uiState.customHeaders.map { header ->
                            ServerConfigurationCustomHeader(
                                name = header.name,
                                value = header.value,
                            )
                        },
                    ),
                )
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun updateHeader(
        id: Long,
        transform: (ServerConfigurationHeaderUiState) -> ServerConfigurationHeaderUiState,
    ) {
        _uiState.update { uiState ->
            uiState.copy(
                customHeaders = uiState.customHeaders.map { header ->
                    if (header.id == id) transform(header) else header
                },
            )
        }
    }
}
