package fr.cassette.cassette.domain.models

data class AlbumDetail(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val created: String?,
    val tracks: List<Track>,
)
