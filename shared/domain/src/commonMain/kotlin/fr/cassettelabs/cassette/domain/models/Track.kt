package fr.cassettelabs.cassette.domain.models

data class Track(
    val id: String,
    val title: String,
    val artist: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
    val albumId: String? = null,
    val albumName: String? = null,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
)
