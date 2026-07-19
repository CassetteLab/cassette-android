package fr.cassette.cassette.data.remote.dto

import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.domain.models.Track
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
    val album: AlbumDto? = null,
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
    val song: List<SongDto> = emptyList(),
) {
    fun toListDomain(): AlbumList = AlbumList(
        id = id,
        name = name,
        artist = artist?.takeIf { it.isNotBlank() },
        coverArt = coverArt?.takeIf { it.isNotBlank() },
        coverArtFilePath = null,
        created = created?.takeIf { it.isNotBlank() },
    )

    fun toDetailDomain(): AlbumDetail = AlbumDetail(
        id = id,
        name = name,
        artist = artist?.takeIf { it.isNotBlank() },
        coverArt = coverArt?.takeIf { it.isNotBlank() },
        coverArtFilePath = null,
        created = created?.takeIf { it.isNotBlank() },
        tracks = song.map { it.toDomain() },
    )
}

@Serializable
internal data class SongDto(
    val id: String,
    val title: String,
    val artist: String? = null,
    val track: Int? = null,
    val duration: Int? = null,
) {
    fun toDomain(): Track = Track(
        id = id,
        title = title,
        artist = artist?.takeIf { it.isNotBlank() },
        trackNumber = track,
        durationSeconds = duration,
    )
}
