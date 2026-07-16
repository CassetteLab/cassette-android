package fr.cassette.cassette.domain.models

data class AlbumCoverArtRequest(
    val url: String,
    val headers: Map<String, String> = emptyMap(),
)
