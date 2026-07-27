package fr.cassettelabs.cassette.domain.models

data class PlaylistList(
    val id: String,
    val name: String,
    val trackCount: Int = 0,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val seedColor: Int? = null,
)
