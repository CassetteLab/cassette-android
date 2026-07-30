package fr.cassettelabs.cassette.domain.models

data class PlaybackContext(
    val type: PlaybackContextType,
    val id: String,
)

enum class PlaybackContextType {
    Album,
    Playlist,
}
