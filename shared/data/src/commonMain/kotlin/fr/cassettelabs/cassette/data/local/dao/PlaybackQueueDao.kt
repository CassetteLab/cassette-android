package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassettelabs.cassette.data.local.embeddeds.PlaybackQueueItemWithTrack
import fr.cassettelabs.cassette.data.local.entities.PlaybackQueueItemEntity
import fr.cassettelabs.cassette.data.local.entities.PlaybackSessionEntity

@Dao
internal interface PlaybackQueueDao {
    @Query("SELECT * FROM playback_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSession(sessionId: String): PlaybackSessionEntity?

    @Query("SELECT * FROM playback_queue_items WHERE sessionId = :sessionId ORDER BY section, position")
    suspend fun getItems(sessionId: String): List<PlaybackQueueItemEntity>

    @Query(
        """
        SELECT
            queue.id AS id,
            queue.sessionId AS sessionId,
            queue.section AS section,
            queue.source AS source,
            queue.position AS position,
            queue.trackId AS trackId,
            queue.addedAt AS addedAt,
            tracks.title AS title,
            tracks.artist AS artist,
            tracks.trackNumber AS trackNumber,
            tracks.durationSeconds AS durationSeconds,
            albums.id AS albumId,
            albums.name AS albumName,
            albums.coverArt AS coverArtId,
            albums.coverArtFilePath AS coverArtFilePath
        FROM playback_queue_items AS queue
        INNER JOIN tracks ON tracks.id = queue.trackId
        INNER JOIN albums ON albums.id = tracks.albumId
        WHERE queue.sessionId = :sessionId
        ORDER BY queue.section, queue.position
        """,
    )
    suspend fun getItemsWithTracks(sessionId: String): List<PlaybackQueueItemWithTrack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PlaybackSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PlaybackQueueItemEntity>)

    @Query("DELETE FROM playback_queue_items WHERE sessionId = :sessionId")
    suspend fun deleteItems(sessionId: String)

    @Query("UPDATE playback_sessions SET currentPositionMs = :positionMs, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateCurrentPosition(
        sessionId: String,
        positionMs: Long,
        updatedAt: Long,
    )

    @Query("UPDATE playback_sessions SET isShuffleEnabled = :isEnabled, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateShuffleEnabled(
        sessionId: String,
        isEnabled: Boolean,
        updatedAt: Long,
    )

    @Query("UPDATE playback_sessions SET repeatMode = :repeatMode, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateRepeatMode(
        sessionId: String,
        repeatMode: String,
        updatedAt: Long,
    )

    @Transaction
    suspend fun replaceQueue(
        session: PlaybackSessionEntity,
        items: List<PlaybackQueueItemEntity>,
    ) {
        insertSession(session)
        deleteItems(session.id)
        insertItems(items)
    }
}
