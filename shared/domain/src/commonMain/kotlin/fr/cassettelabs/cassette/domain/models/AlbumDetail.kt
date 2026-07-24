package fr.cassettelabs.cassette.domain.models

data class AlbumDetail(
    val id: String,
    val name: String,
    val artist: String?,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val tracks: List<Track>,
    val seedColor: Int? = null,
)
