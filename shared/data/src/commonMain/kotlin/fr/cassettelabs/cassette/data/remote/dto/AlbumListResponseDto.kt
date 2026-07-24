package fr.cassettelabs.cassette.data.remote.dto

import fr.cassettelabs.cassette.domain.models.AlbumList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AlbumListResponseDto(
    @SerialName("subsonic-response")
    val subsonicResponse: AlbumListSubsonicResponseDto,
)

@Serializable
internal data class AlbumListSubsonicResponseDto(
    val status: String,
    val albumList2: AlbumListDto? = null,
)

@Serializable
internal data class AlbumListDto(
    val album: List<AlbumDto> = emptyList(),
)

@Serializable
internal data class AlbumDto(
    val id: String,
    val name: String,
    val artist: String? = null,
    val coverArt: String? = null,
    val created: String? = null,
) {
    fun toListDomain(): AlbumList =
        AlbumList(
            id = id,
            name = name,
            artist = artist?.takeIf { it.isNotBlank() },
            coverArt = coverArt?.takeIf { it.isNotBlank() },
            coverArtFilePath = null,
            created = created?.takeIf { it.isNotBlank() },
        )
}
