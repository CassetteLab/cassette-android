package fr.cassettelabs.cassette.data.local.embeddeds

internal data class PlaylistWithCoverArt(
    val id: String,
    val serverConfigurationId: Long,
    val name: String,
    val trackCount: Int,
    val coverArt: String?,
    val coverArtFilePath: String?,
    val created: String?,
    val seedColor: Int?,
)
