package fr.cassette.cassette.data.remote.dto

import fr.cassette.cassette.domain.models.Album
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
    fun toDomain(): Album = Album(
        id = id,
        name = name,
        artist = artist?.takeIf { it.isNotBlank() },
        coverArt = coverArt?.takeIf { it.isNotBlank() },
        created = created?.takeIf { it.isNotBlank() },
    )
}
