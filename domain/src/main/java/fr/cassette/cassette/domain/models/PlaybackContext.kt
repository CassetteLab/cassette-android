package fr.cassette.cassette.domain.models

data class PlaybackContext(
    val type: PlaybackContextType,
    val id: String,
)

enum class PlaybackContextType {
    Album,
}
