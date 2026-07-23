package fr.cassette.cassette.presentation.playlistDetail

import androidx.lifecycle.viewModelScope
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.domain.models.AlbumCoverArt
import fr.cassette.cassette.domain.models.CurrentTrack
import fr.cassette.cassette.domain.models.PlaybackContext
import fr.cassette.cassette.domain.models.PlaybackContextType
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetPlaylistTracksUseCase
import fr.cassette.cassette.domain.usecases.GetPlaylistUseCase
import fr.cassette.cassette.domain.usecases.PlayTrackUseCase
import fr.cassette.cassette.presentation.core.mvi.BaseViewModel
import kotlinx.coroutines.launch

internal class PlaylistDetailViewModel(
    private val playlistId: String,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
    private val getAlbumCoverArtUseCase: GetAlbumCoverArtUseCase,
    private val getPlaylistCoverArtUseCase: GetPlaylistCoverArtUseCase,
    private val playTrackUseCase: PlayTrackUseCase,
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
        }
    }

    private fun playTrack(trackId: String) {
        val playlist = uiState.value.playlist ?: return
        val contextTracks =
            uiState.value.tracks.map { track ->
                CurrentTrack(
                    track = track,
                    albumId = track.albumId ?: playlist.id,
                    albumName = track.albumName,
                    coverArtId = track.coverArt,
                    coverArtFilePath = track.coverArtFilePath,
                )
            }
        contextTracks
            .firstOrNull { it.track.id == trackId }
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
                val coverArt =
                    playlist.coverArtFilePath?.let { filePath -> AlbumCoverArt(filePath = filePath) } ?: runCatching {
                        getPlaylistCoverArtUseCase(
                            coverArtId = playlist.coverArt ?: playlist.id,
                            size = COVER_ART_SIZE,
                            playlistId = playlist.id,
                        )
                    }.getOrNull()
                updateState { it.copy(isLoading = false, playlist = playlist, coverArt = coverArt) }
            } catch (exception: Exception) {
                logger.w("Unable to load playlist $playlistId", exception)
                updateState { it.copy(isLoading = false) }
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
                logger.w("Unable to load playlist tracks $playlistId", exception)
                updateState { it.copy(isTracksLoading = false) }
            }
        }
    }

    private suspend fun downloadMissingTrackCoverArts(tracks: List<Track>) {
        tracks.forEach { track ->
            val coverArtId = track.coverArt ?: return@forEach
            val coverArt =
                runCatching {
                    getAlbumCoverArtUseCase(
                        coverArtId = coverArtId,
                        size = TRACK_COVER_ART_SIZE,
                        albumId = track.albumId,
                    )
                }.getOrNull() ?: return@forEach

            updateState { state ->
                state.copy(
                    tracks =
                        state.tracks.map { stateTrack ->
                            if (stateTrack.id == track.id) {
                                stateTrack.copy(coverArtFilePath = coverArt.filePath)
                            } else {
                                stateTrack
                            }
                        },
                )
            }
        }
    }

    private companion object {
        const val COVER_ART_SIZE = 600
        const val TRACK_COVER_ART_SIZE = 160
    }
}
