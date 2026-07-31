package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.entities.TrackEntity

@Dao
internal interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE id = :trackId LIMIT 1")
    suspend fun getTrack(trackId: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE albumId = :albumId ORDER BY trackNumber, title")
    suspend fun getAlbumTracks(albumId: String): List<TrackEntity>

    @Query("UPDATE tracks SET starredAt = :starredAt WHERE id = :trackId")
    suspend fun updateStarredAt(
        trackId: String,
        starredAt: String?,
    )

    @Query("DELETE FROM tracks WHERE albumId = :albumId")
    suspend fun deleteAlbumTracks(albumId: String)
}
