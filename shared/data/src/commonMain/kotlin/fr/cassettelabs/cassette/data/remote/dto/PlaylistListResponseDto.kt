package fr.cassettelabs.cassette.data.remote.dto

import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.domain.models.Track
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
    val playlist: PlaylistDto? = null,
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
    val entry: List<PlaylistSongDto> = emptyList(),
    val song: List<PlaylistSongDto> = emptyList(),
    val child: List<PlaylistSongDto> = emptyList(),
) {
    fun toDomain(): Playlist =
        Playlist(
            id = id,
            name = name,
            trackCount = songCount,
            coverArt = coverArt?.takeIf { it.isNotBlank() },
            coverArtFilePath = null,
            created = created?.takeIf { it.isNotBlank() },
        )

    fun tracksToDomain(): List<Track> = (entry.ifEmpty { song }.ifEmpty { child }).map { it.toDomain() }
}

@Serializable
internal data class PlaylistSongDto(
    val id: String,
    val title: String,
    val artist: String? = null,
    val albumId: String? = null,
    val album: String? = null,
    val coverArt: String? = null,
    val track: Int? = null,
    val duration: Int? = null,
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
        )
}
