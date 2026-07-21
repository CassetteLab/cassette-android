package fr.cassette.cassette.domain.models

data class PlaylistList(
    val id: String,
    val name: String,
    val trackCount: Int = 0,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
)
