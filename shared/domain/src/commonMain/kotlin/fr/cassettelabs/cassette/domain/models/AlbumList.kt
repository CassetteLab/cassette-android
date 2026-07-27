package fr.cassettelabs.cassette.domain.models

data class AlbumList(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val seedColor: Int? = null,
)
