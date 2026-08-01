package fr.cassettelabs.cassette.presentation.artistDetail

import fr.cassettelabs.cassette.presentation.core.mvi.Event

internal sealed interface ArtistDetailEvent : Event {
    data object OnAppearing : ArtistDetailEvent

    data object OnRefresh : ArtistDetailEvent

    data object OnBackClicked : ArtistDetailEvent

    data class OnAlbumClicked(
        val albumId: String,
    ) : ArtistDetailEvent
}
