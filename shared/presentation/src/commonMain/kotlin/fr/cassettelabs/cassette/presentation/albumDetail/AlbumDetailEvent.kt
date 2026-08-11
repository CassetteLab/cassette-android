package fr.cassettelabs.cassette.presentation.albumDetail

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface AlbumDetailEvent : Event {
    data object OnAppearing : AlbumDetailEvent

    data object OnRefresh : AlbumDetailEvent

    data object OnBackClicked : AlbumDetailEvent

    data class OnTrackClicked(
        val trackId: String,
    ) : AlbumDetailEvent

    data class OnArtistClicked(
        val artistId: String,
    ) : AlbumDetailEvent

    data class OnTrackMoreClicked(
        val trackId: String,
    ) : AlbumDetailEvent

    data class OnLikeTrack(
        val trackId: String,
    ) : AlbumDetailEvent

    data class OnAddToPlaylist(
        val trackId: String,
    ) : AlbumDetailEvent

    data class OnAddToQueue(
        val trackId: String,
    ) : AlbumDetailEvent
}
