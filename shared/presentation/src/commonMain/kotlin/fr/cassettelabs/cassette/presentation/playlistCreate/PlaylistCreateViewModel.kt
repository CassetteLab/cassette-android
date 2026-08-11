package fr.cassettelabs.cassette.presentation.playlistCreate

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistCreate.CreatePlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistCreate.GetAllTracksUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class PlaylistCreateViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getAllTracksUseCase: GetAllTracksUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistCreateUiState, PlaylistCreateEvent>(
    viewModelName = "PlaylistCreateViewModel",
    logger = logger,
    initialState = PlaylistCreateUiState(),
) {
    private val coverArtJobs = mutableMapOf<String, Job>()

    override fun handleEvent(event: PlaylistCreateEvent) {
        when (event) {
            PlaylistCreateEvent.OnAppearing -> {
                loadTracks()
            }
            PlaylistCreateEvent.OnBackClicked -> Unit
            is PlaylistCreateEvent.OnNameChanged -> {
                updateState { it.copy(name = event.name) }
            }
            is PlaylistCreateEvent.OnTrackToggled -> {
                val currentIds = uiState.value.selectedTrackIds
                val newIds = if (event.trackId in currentIds) {
                    currentIds - event.trackId
                } else {
                    currentIds + event.trackId
                }
                updateState { it.copy(selectedTrackIds = newIds) }
            }
            is PlaylistCreateEvent.OnTrackCoverArtAppeared -> downloadTrackCoverArtIfNeeded(event.trackId)
            PlaylistCreateEvent.OnCreateClicked -> {
                createPlaylist()
            }
        }
    }

    private fun loadTracks() {
        if (uiState.value.tracks.isNotEmpty()) return

        viewModelScope.launch {
            updateState { it.copy(isLoadingTracks = true) }
            try {
                val tracks = getAllTracksUseCase()
                updateState { it.copy(isLoadingTracks = false, tracks = tracks) }
            } catch (e: Exception) {
                logger.w("Unable to load tracks: ${e.message}")
                updateState { it.copy(isLoadingTracks = false) }
            }
        }
    }

    private fun downloadTrackCoverArtIfNeeded(trackId: String) {
        val state = uiState.value
        val track = state.tracks.firstOrNull { it.id == trackId } ?: return
        val currentStatus = state.trackCoverArtStatuses[trackId]

        if (track.coverArtFilePath != null || currentStatus != null || coverArtJobs[trackId]?.isActive == true) return

        val coverArtId = track.coverArt ?: return
        coverArtJobs[trackId] = getAlbumCoverArtUseCase(
            coverArtId = coverArtId,
            size = TRACK_COVER_ART_SIZE,
            albumId = track.albumId,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load cover art for track ${track.id}: ${status.throwable.message}")
            }
            updateState { currentState ->
                currentState.copy(trackCoverArtStatuses = currentState.trackCoverArtStatuses + (track.id to status))
            }
        }.launchIn(viewModelScope).also { job ->
            job.invokeOnCompletion { coverArtJobs.remove(trackId) }
        }
    }

    private fun createPlaylist() {
        val state = uiState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val createdPlaylist = createPlaylistUseCase(state.name, state.selectedTrackIds.toList())
                updateState { it.copy(isLoading = false, createdPlaylist = createdPlaylist) }
            } catch (e: Exception) {
                logger.w("Unable to create playlist: ${e.message}")
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private companion object {
        const val TRACK_COVER_ART_SIZE = 160
    }
}
