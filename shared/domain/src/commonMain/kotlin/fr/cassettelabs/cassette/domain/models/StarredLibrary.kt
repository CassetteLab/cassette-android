package fr.cassettelabs.cassette.domain.models

data class StarredLibrary(
    val albums: List<Album>,
    val tracks: List<Track>,
)
