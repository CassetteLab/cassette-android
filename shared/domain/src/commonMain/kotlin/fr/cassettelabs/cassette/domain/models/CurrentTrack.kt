package fr.cassettelabs.cassette.domain.models

data class CurrentTrack(
    val track: Track,
    val albumId: String,
    val albumName: String?,
    val coverArtId: String?,
    val coverArtFilePath: String?,
)
