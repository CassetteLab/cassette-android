package fr.cassette.cassette.domain.models

data class Track(
    val id: String,
    val title: String,
    val artist: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
)
