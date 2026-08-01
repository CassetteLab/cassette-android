package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.embeddeds.PlaylistWithCoverArt
import fr.cassettelabs.cassette.data.local.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylist(playlistId: String): PlaylistEntity?

    @Query(
        """
        SELECT playlists.*, cover_arts.filePath AS coverArtFilePath
        FROM playlists
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = playlists.serverConfigurationId
            AND cover_arts.coverArtId = playlists.coverArt
            AND cover_arts.size = :coverArtSize
        WHERE playlists.id = :playlistId
        LIMIT 1
        """,
    )
    suspend fun getPlaylistWithCoverArt(
        playlistId: String,
        coverArtSize: Int,
    ): PlaylistWithCoverArt?

    @Query(
        """
        SELECT playlists.*, cover_arts.filePath AS coverArtFilePath
        FROM playlists
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = playlists.serverConfigurationId
            AND cover_arts.coverArtId = playlists.coverArt
            AND cover_arts.size = :coverArtSize
        ORDER BY playlists.created DESC
        """,
    )
    fun getAllPlaylists(coverArtSize: Int): Flow<List<PlaylistWithCoverArt>>

    @Query("DELETE FROM playlists WHERE serverConfigurationId = :serverConfigurationId")
    suspend fun deleteAllPlaylists(serverConfigurationId: Long)

    @Query("DELETE FROM playlists WHERE serverConfigurationId = :serverConfigurationId AND id NOT IN (:playlistIds)")
    suspend fun deletePlaylistsNotIn(
        serverConfigurationId: Long,
        playlistIds: List<String>,
    )

    @Query("UPDATE playlists SET seedColor = :seedColor WHERE id = :playlistId")
    suspend fun updateSeedColor(
        playlistId: String,
        seedColor: Int,
    )
}
