package fr.cassette.cassette.presentation.core.serverConfiguration

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.models.ServerConfiguration
import fr.cassette.cassette.domain.models.ServerConfigurationCustomHeader
import fr.cassette.cassette.domain.usecases.PingServerUseCase
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class ServerConfigurationViewModel(
    private val pingServerUseCase: PingServerUseCase,
    private val saveServerConfigurationUseCase: SaveServerConfigurationUseCase,
    logger: Logger,
) : BaseViewModel<ServerConfigurationUiState, ServerConfigurationEvent>(
    viewModelName = "ServerConfigurationViewModel",
    logger = logger,
    initialState = ServerConfigurationUiState(),
) {

    private var nextHeaderId = 0L

    override fun handleEvent(event: ServerConfigurationEvent) {
        when (event) {
            is ServerConfigurationEvent.OnServerUrlChanged -> updateState { uiState ->
                uiState.copy(serverUrl = event.value, error = null, isSaved = false)
            }

            is ServerConfigurationEvent.OnUsernameChanged -> updateState { uiState ->
                uiState.copy(username = event.value, error = null, isSaved = false)
            }

            is ServerConfigurationEvent.OnPasswordChanged -> updateState { uiState ->
                uiState.copy(password = event.value, error = null, isSaved = false)
            }

            ServerConfigurationEvent.OnAddHeaderClicked -> updateState { uiState ->
                uiState.copy(
                    customHeaders = uiState.customHeaders + ServerConfigurationHeaderUiState(id = nextHeaderId++),
                    error = null,
                    isSaved = false,
                )
            }

            is ServerConfigurationEvent.OnRemoveHeaderClicked -> updateState { uiState ->
                uiState.copy(
                    customHeaders = uiState.customHeaders.filterNot { it.id == event.id },
                    error = null,
                    isSaved = false,
                )
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
        val currentUiState = uiState.value
        if (!currentUiState.canSubmit) return
        val serverConfiguration = currentUiState.toServerConfiguration()

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null, isSaved = false) }
            try {
                pingServerUseCase(serverConfiguration)
                saveServerConfigurationUseCase(serverConfiguration)
                updateState { it.copy(isLoading = false, isSaved = true) }
            } catch (_: Exception) {
                updateState {
                    it.copy(
                        isLoading = false,
                        error = ServerConfigurationError.ConnectionFailed,
                        isSaved = false,
                    )
                }
            }
        }
    }

    private fun updateHeader(
        id: Long,
        transform: (ServerConfigurationHeaderUiState) -> ServerConfigurationHeaderUiState,
    ) {
        updateState { uiState ->
            uiState.copy(
                customHeaders = uiState.customHeaders.map { header ->
                    if (header.id == id) transform(header) else header
                },
                error = null,
                isSaved = false,
            )
        }
    }

    private fun ServerConfigurationUiState.toServerConfiguration(): ServerConfiguration = ServerConfiguration(
        serverUrl = serverUrl,
        username = username,
        password = password,
        customHeaders = customHeaders.map { header ->
            ServerConfigurationCustomHeader(
                name = header.name,
                value = header.value,
            )
        },
    )
}
