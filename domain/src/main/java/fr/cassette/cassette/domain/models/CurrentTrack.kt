package fr.cassette.cassette.domain.models

data class CurrentTrack(
    val track: Track,
    val albumName: String?,
    val coverArtId: String?,
)
