package fr.cassettelabs.cassette.data.remote.dto

import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.Track
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
    val starred2: StarredDto? = null,
    val album: AlbumDto? = null,
)

@Serializable
internal data class AlbumListDto(
    val album: List<AlbumDto> = emptyList(),
)

@Serializable
internal data class StarredDto(
    val album: List<AlbumDto> = emptyList(),
    val song: List<SongDto> = emptyList(),
)

@Serializable
internal data class AlbumDto(
    val id: String,
    val name: String,
    val artist: String? = null,
    val coverArt: String? = null,
    val created: String? = null,
    val starred: String? = null,
    val song: List<SongDto> = emptyList(),
) {
    fun toDomain(): Album =
        Album(
            id = id,
            name = name,
            artist = artist?.takeIf { it.isNotBlank() },
            coverArt = coverArt?.takeIf { it.isNotBlank() },
            coverArtFilePath = null,
            created = created?.takeIf { it.isNotBlank() },
            starredAt = starred?.takeIf { it.isNotBlank() },
        )

    fun tracksToDomain(): List<Track> = song.map { it.toDomain() }
}

@Serializable
internal data class SongDto(
    val id: String,
    val title: String,
    val artist: String? = null,
    val albumId: String? = null,
    val album: String? = null,
    val coverArt: String? = null,
    val track: Int? = null,
    val duration: Int? = null,
    val starred: String? = null,
) {
    fun toDomain(): Track =
        Track(
            id = id,
            title = title,
            artist = artist?.takeIf { it.isNotBlank() },
            trackNumber = track,
            durationSeconds = duration,
            albumId = albumId?.takeIf { it.isNotBlank() },
            albumName = album?.takeIf { it.isNotBlank() },
            coverArt = coverArt?.takeIf { it.isNotBlank() },
            starredAt = starred?.takeIf { it.isNotBlank() },
        )
}
