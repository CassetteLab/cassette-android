package fr.cassettelabs.cassette.domain.models

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int = 0,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
)
