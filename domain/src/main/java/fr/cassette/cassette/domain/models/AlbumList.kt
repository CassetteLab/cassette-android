package fr.cassette.cassette.domain.models

data class AlbumList(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val created: String?,
)
