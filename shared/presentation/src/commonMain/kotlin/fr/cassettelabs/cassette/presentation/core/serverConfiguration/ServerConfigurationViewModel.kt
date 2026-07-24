package fr.cassettelabs.cassette.presentation.core.serverConfiguration

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.ServerConfiguration
import fr.cassettelabs.cassette.domain.models.ServerConfigurationCustomHeader
import fr.cassettelabs.cassette.domain.usecases.GetServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.PingServerUseCase
import fr.cassettelabs.cassette.domain.usecases.SaveServerConfigurationUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class ServerConfigurationViewModel(
    private val pingServerUseCase: PingServerUseCase,
    private val getServerConfigurationUseCase: GetServerConfigurationUseCase,
    private val saveServerConfigurationUseCase: SaveServerConfigurationUseCase,
    logger: Logger,
) : BaseViewModel<ServerConfigurationUiState, ServerConfigurationEvent>(
        viewModelName = "ServerConfigurationViewModel",
        logger = logger,
        initialState = ServerConfigurationUiState(),
    ) {
    private var nextHeaderId = 0L

    init {
        loadServerConfiguration()
    }

    override fun handleEvent(event: ServerConfigurationEvent) {
        when (event) {
            is ServerConfigurationEvent.OnServerUrlChanged -> updateState { it.copy(serverUrl = event.value, error = null, isSaved = false) }
            is ServerConfigurationEvent.OnUsernameChanged -> updateState { it.copy(username = event.value, error = null, isSaved = false) }
            is ServerConfigurationEvent.OnPasswordChanged -> updateState { it.copy(password = event.value, error = null, isSaved = false) }
            ServerConfigurationEvent.OnAddHeaderClicked ->
                updateState { uiState ->
                    uiState.copy(
                        customHeaders = uiState.customHeaders + ServerConfigurationHeaderUiState(id = nextHeaderId++),
                        error = null,
                        isSaved = false,
                    )
                }
            is ServerConfigurationEvent.OnRemoveHeaderClicked ->
                updateState { uiState -> uiState.copy(customHeaders = uiState.customHeaders.filterNot { it.id == event.id }, error = null, isSaved = false) }
            is ServerConfigurationEvent.OnHeaderNameChanged -> updateHeader(event.id) { it.copy(name = event.value) }
            is ServerConfigurationEvent.OnHeaderValueChanged -> updateHeader(event.id) { it.copy(value = event.value) }
            is ServerConfigurationEvent.OnHeaderValueVisibilityChanged -> updateHeader(event.id) { it.copy(isValueVisible = event.isVisible) }
            ServerConfigurationEvent.OnConnectClicked -> saveServerConfiguration()
            ServerConfigurationEvent.OnBackClicked -> Unit
        }
    }

    private fun loadServerConfiguration() {
        viewModelScope.launch {
            val serverConfiguration = getServerConfigurationUseCase() ?: return@launch
            val customHeaders =
                serverConfiguration.customHeaders.mapIndexed { index, customHeader ->
                    ServerConfigurationHeaderUiState(
                        id = index.toLong(),
                        name = customHeader.name,
                        value = customHeader.value,
                    )
                }
            nextHeaderId = customHeaders.size.toLong()
            updateState {
                it.copy(
                    serverUrl = serverConfiguration.serverUrl,
                    username = serverConfiguration.username,
                    password = serverConfiguration.password,
                    customHeaders = customHeaders,
                )
            }
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
                updateState { it.copy(isLoading = false, error = ServerConfigurationError.ConnectionFailed, isSaved = false) }
            }
        }
    }

    private fun updateHeader(
        id: Long,
        transform: (ServerConfigurationHeaderUiState) -> ServerConfigurationHeaderUiState,
    ) {
        updateState { uiState ->
            uiState.copy(
                customHeaders = uiState.customHeaders.map { header -> if (header.id == id) transform(header) else header },
                error = null,
                isSaved = false,
            )
        }
    }

    private fun ServerConfigurationUiState.toServerConfiguration(): ServerConfiguration =
        ServerConfiguration(
            serverUrl = serverUrl,
            username = username,
            password = password,
            customHeaders = customHeaders.map { ServerConfigurationCustomHeader(name = it.name, value = it.value) },
        )
}
