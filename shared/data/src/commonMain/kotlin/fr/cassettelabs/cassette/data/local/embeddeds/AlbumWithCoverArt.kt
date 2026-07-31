package fr.cassettelabs.cassette.data.local.embeddeds

internal data class AlbumWithCoverArt(
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val artist: String?,
    val artistId: String?,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val seedColor: Int?,
)
