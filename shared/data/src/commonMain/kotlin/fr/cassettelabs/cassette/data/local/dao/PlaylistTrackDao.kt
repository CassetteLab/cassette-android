package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassettelabs.cassette.data.local.embeddeds.TrackWithAlbumAndCoverArt
import fr.cassettelabs.cassette.data.local.entities.PlaylistTrackEntity

@Dao
internal interface PlaylistTrackDao {
    @Query(
        """
        SELECT
            tracks.id AS trackId,
            tracks.title AS title,
            tracks.artist AS artist,
            tracks.artistId AS artistId,
            album_tracks.trackNumber AS trackNumber,
            tracks.durationSeconds AS durationSeconds,
            album_tracks.albumId AS albumId,
            COALESCE(albums.name, tracks.albumName) AS albumName,
            COALESCE(albums.coverArt, tracks.coverArt) AS coverArt,
            cover_arts.filePath AS coverArtFilePath,
            tracks.starredAt AS starredAt
        FROM playlist_tracks
        INNER JOIN tracks ON tracks.id = playlist_tracks.trackId
        LEFT JOIN album_tracks ON album_tracks.trackId = tracks.id
        LEFT JOIN albums ON albums.id = album_tracks.albumId
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = albums.serverConfigurationId
            AND cover_arts.coverArtId = COALESCE(albums.coverArt, tracks.coverArt)
            AND cover_arts.size = :coverArtSize
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position
        """,
    )
    suspend fun getPlaylistTracks(
        playlistId: String,
        coverArtSize: Int,
    ): List<TrackWithAlbumAndCoverArt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTracks(tracks: List<PlaylistTrackEntity>)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deletePlaylistTracks(playlistId: String)

    @Transaction
    suspend fun replacePlaylistTracks(
        playlistId: String,
        tracks: List<PlaylistTrackEntity>,
    ) {
        deletePlaylistTracks(playlistId)
        insertPlaylistTracks(tracks)
    }
}
