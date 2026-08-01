package fr.cassettelabs.cassette.data.local.embeddeds

internal data class TrackWithAlbumAndCoverArt(
    val trackId: String,
    val title: String,
    val artist: String?,
    val artistId: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
    val albumId: String?,
    val albumName: String?,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val starredAt: String?,
)
