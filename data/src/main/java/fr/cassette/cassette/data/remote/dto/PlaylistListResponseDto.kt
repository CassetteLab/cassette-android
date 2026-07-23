package fr.cassette.cassette.data.remote.dto

import fr.cassette.cassette.domain.models.PlaylistList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaylistListResponseDto(
    @SerialName("subsonic-response")
    val subsonicResponse: PlaylistListSubsonicResponseDto,
)

@Serializable
internal data class PlaylistListSubsonicResponseDto(
    val status: String,
    val playlists: PlaylistsDto? = null,
)

@Serializable
internal data class PlaylistsDto(
    val playlist: List<PlaylistDto> = emptyList(),
)

@Serializable
internal data class PlaylistDto(
    val id: String,
    val name: String,
    val songCount: Int = 0,
    val coverArt: String? = null,
    val created: String? = null,
) {
    fun toListDomain(): PlaylistList =
        PlaylistList(
            id = id,
            name = name,
            trackCount = songCount,
            coverArt = coverArt?.takeIf { it.isNotBlank() },
            coverArtFilePath = null,
            created = created?.takeIf { it.isNotBlank() },
        )
}
