package fr.cassettelabs.cassette.domain.models

data class Album(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val artistId: String? = null,
    val seedColor: Int? = null,
    val starredAt: String? = null,
)
