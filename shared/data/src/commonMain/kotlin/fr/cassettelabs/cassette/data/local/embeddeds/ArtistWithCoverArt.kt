package fr.cassettelabs.cassette.data.local.embeddeds

internal data class ArtistWithCoverArt(
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val albumCount: Int,
    val coverArt: String?,
    val coverArtFilePath: String?,
)
