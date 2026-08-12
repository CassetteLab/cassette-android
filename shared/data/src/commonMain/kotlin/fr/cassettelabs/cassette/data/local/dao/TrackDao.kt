package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.entities.TrackEntity
import fr.cassettelabs.cassette.domain.models.Track

@Dao
internal interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE id = :trackId LIMIT 1")
    suspend fun getTrack(trackId: String): TrackEntity?

    @Query("UPDATE tracks SET starredAt = :starredAt WHERE id = :trackId")
    suspend fun updateStarredAt(
        trackId: String,
        starredAt: String?,
    )

    @Query(
        """
        SELECT tracks.id, tracks.title, tracks.artist, NULL AS trackNumber,
               tracks.artistId, tracks.durationSeconds, NULL AS albumId,
               tracks.albumName, tracks.coverArt, cover_arts.filePath AS coverArtFilePath,
               tracks.starredAt
        FROM tracks
        LEFT JOIN cover_arts ON cover_arts.coverArtId = tracks.coverArt AND cover_arts.size = :coverArtSize
        ORDER BY tracks.title
        """,
    )
    suspend fun getAllTracks(coverArtSize: Int): List<Track>
}
