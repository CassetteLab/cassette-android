package fr.cassettelabs.cassette.presentation.playlistCreate

import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.mvi.UiState

internal data class PlaylistCreateUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val createdPlaylist: Playlist? = null,
    val isLoadingTracks: Boolean = false,
    val tracks: List<Track> = emptyList(),
    val selectedTrackIds: Set<String> = emptySet(),
    val trackCoverArtStatuses: Map<String, CoverArtLoadingStatus> = emptyMap(),
) : UiState {
    val canCreate: Boolean get() = name.isNotBlank()
}
