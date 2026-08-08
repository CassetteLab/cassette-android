package fr.cassettelabs.cassette.presentation.playlistCreate

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.usecases.playlistCreate.CreatePlaylistUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class PlaylistCreateViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistCreateUiState, PlaylistCreateEvent>(
    viewModelName = "PlaylistCreateViewModel",
    logger = logger,
    initialState = PlaylistCreateUiState(),
) {
    override fun handleEvent(event: PlaylistCreateEvent) {
        when (event) {
            PlaylistCreateEvent.OnBackClicked -> Unit
            is PlaylistCreateEvent.OnNameChanged -> {
                updateState { it.copy(name = event.name) }
            }
            PlaylistCreateEvent.OnCreateClicked -> {
                createPlaylist()
            }
        }
    }

    private fun createPlaylist() {
        val name = uiState.value.name
        if (name.isBlank()) return

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                createPlaylistUseCase(name)
                updateState { it.copy(isLoading = false, isCreated = true) }
            } catch (e: Exception) {
                logger.w("Unable to create playlist: ${e.message}")
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}
