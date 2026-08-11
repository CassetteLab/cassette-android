package fr.cassettelabs.cassette.presentation.playlistDetail

import androidx.lifecycle.viewModelScope
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.PlaybackContext
import fr.cassettelabs.cassette.domain.models.PlaybackContextType
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistDetail.GetPlaylistTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.DeletePlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PlayTrackUseCase
import fr.cassettelabs.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class PlaylistDetailViewModel(
    private val playlistId: String,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val getPlaylistCoverArtUseCase: GetPlaylistCoverArtUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    logger: Logger,
) : BaseViewModel<PlaylistDetailUiState, PlaylistDetailEvent>(
        viewModelName = "PlaylistDetailViewModel",
        logger = logger,
        initialState = PlaylistDetailUiState(playlistId = playlistId),
    ) {
    override fun handleEvent(event: PlaylistDetailEvent) {
        when (event) {
            PlaylistDetailEvent.OnAppearing -> {
                loadPlaylist()
                loadPlaylistTracks()
            }
            PlaylistDetailEvent.OnBackClicked -> Unit
            is PlaylistDetailEvent.OnTrackClicked -> playTrack(event.trackId)
            PlaylistDetailEvent.OnMenuClicked -> Unit
            PlaylistDetailEvent.OnDeletePlaylist -> deletePlaylist()
            is PlaylistDetailEvent.OnLikeTrack -> Unit
            is PlaylistDetailEvent.OnAddToPlaylist -> Unit
            is PlaylistDetailEvent.OnAddToQueue -> Unit
        }
    }

    private fun playTrack(trackId: String) {
        val playlist = uiState.value.playlist ?: return
        val contextTracks =
            uiState.value.tracks.map { track ->
                track.copy(
                    albumId = track.albumId ?: playlist.id,
                    albumName = track.albumName,
                    coverArt = track.coverArt,
                    coverArtFilePath = track.coverArtFilePath,
                )
            }
        contextTracks
            .firstOrNull { it.id == trackId }
            ?.let { currentTrack ->
                viewModelScope.launch {
                    playTrackUseCase(
                        currentTrack = currentTrack,
                        contextTracks = contextTracks,
                        context = PlaybackContext(type = PlaybackContextType.Playlist, id = playlist.id),
                    )
                }
            }
    }

    private fun loadPlaylist() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val playlist = getPlaylistUseCase(playlistId)
                updateState { it.copy(isLoading = false, playlist = playlist) }
                loadPlaylistCoverArt(playlist.id, playlist.coverArt ?: playlist.id)
            } catch (exception: Exception) {
                logger.w("Unable to load playlist $playlistId" + ": " + exception.message)
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun deletePlaylist() {
        viewModelScope.launch {
            updateState { it.copy(isDeleting = true) }
            try {
                deletePlaylistUseCase(playlistId)
            } catch (exception: Exception) {
                logger.w("Unable to delete playlist $playlistId" + ": " + exception.message)
                updateState { it.copy(isDeleting = false) }
            }
        }
    }

    private fun loadPlaylistTracks() {
        viewModelScope.launch {
            updateState { it.copy(isTracksLoading = true) }
            try {
                val tracks = getPlaylistTracksUseCase(playlistId)
                updateState { it.copy(isTracksLoading = false, tracks = tracks) }
                downloadMissingTrackCoverArts(tracks)
            } catch (exception: Exception) {
                logger.w("Unable to load playlist tracks $playlistId" + ": " + exception.message)
                updateState { it.copy(isTracksLoading = false) }
            }
        }
    }

    private suspend fun downloadMissingTrackCoverArts(tracks: List<Track>) {
        tracks.forEach { track ->
            val coverArtId = track.coverArt ?: return@forEach
            getAlbumCoverArtUseCase(
                coverArtId = coverArtId,
                size = TRACK_COVER_ART_SIZE,
                albumId = track.albumId,
            ).onEach { status ->
                if (status is CoverArtLoadingStatus.Error) {
                    logger.w("Unable to load cover art for track ${track.id}" + ": " + status.throwable.message)
                }
                updateState { state ->
                    state.copy(
                        tracks =
                            state.tracks.map { stateTrack ->
                                if (stateTrack.id == track.id && status is CoverArtLoadingStatus.Loaded) {
                                    stateTrack.copy(coverArtFilePath = status.filePath)
                                } else {
                                    stateTrack
                                }
                            },
                        trackCoverArtStatuses = state.trackCoverArtStatuses + (track.id to status),
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun loadPlaylistCoverArt(
        playlistId: String,
        coverArtId: String,
    ) {
        getPlaylistCoverArtUseCase(
            coverArtId = coverArtId,
            size = COVER_ART_SIZE,
            playlistId = playlistId,
        ).onEach { status ->
            if (status is CoverArtLoadingStatus.Error) {
                logger.w("Unable to load cover art for playlist $playlistId" + ": " + status.throwable.message)
            }
            updateState { it.copy(coverArtStatus = status) }
        }.launchIn(viewModelScope)
    }

    private companion object {
        const val COVER_ART_SIZE = 600
        const val TRACK_COVER_ART_SIZE = 160
    }
}
