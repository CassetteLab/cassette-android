package fr.cassette.cassette.domain.models

data class Album(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val created: String?,
)
