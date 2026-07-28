package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassettelabs.cassette.data.local.entities.PlaylistTrackEntity
import fr.cassettelabs.cassette.data.local.entities.TrackEntity

@Dao
internal interface PlaylistTrackDao {
    @Query(
        """
        SELECT tracks.*
        FROM playlist_tracks
        INNER JOIN tracks ON tracks.id = playlist_tracks.trackId
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position
        """,
    )
    suspend fun getPlaylistTracks(playlistId: String): List<TrackEntity>

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
