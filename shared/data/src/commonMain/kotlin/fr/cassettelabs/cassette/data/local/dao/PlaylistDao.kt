package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT * FROM playlists ORDER BY created DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("DELETE FROM playlists WHERE serverConfigurationId = :serverConfigurationId")
    suspend fun deleteAllPlaylists(serverConfigurationId: Long)

    @Query("DELETE FROM playlists WHERE serverConfigurationId = :serverConfigurationId AND id NOT IN (:playlistIds)")
    suspend fun deletePlaylistsNotIn(
        serverConfigurationId: Long,
        playlistIds: List<String>,
    )

    @Query("UPDATE playlists SET coverArtFilePath = :coverArtFilePath WHERE id = :playlistId")
    suspend fun updateCoverArtFilePath(
        playlistId: String,
        coverArtFilePath: String,
    )

    @Query("UPDATE playlists SET seedColor = :seedColor WHERE id = :playlistId")
    suspend fun updateSeedColor(
        playlistId: String,
        seedColor: Int,
    )
}
