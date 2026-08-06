package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface LocalDataDao {
    @Transaction
    suspend fun deleteAllLocalData() {
        deletePlaybackQueueItems()
        deletePlaybackSessions()
        deletePlaylistTracks()
        deleteStarredTracks()
        deleteStarredAlbums()
        deleteAlbumTracks()
        deleteCoverArt()
        deletePlaylists()
        deleteTracks()
        deleteAlbums()
        deleteArtists()
        deleteServerConfigurationCustomHeaders()
        deleteServerConfigurations()
    }

    @Query("DELETE FROM playback_queue_items")
    suspend fun deletePlaybackQueueItems()

    @Query("DELETE FROM playback_sessions")
    suspend fun deletePlaybackSessions()

    @Query("DELETE FROM playlist_tracks")
    suspend fun deletePlaylistTracks()

    @Query("DELETE FROM starred_tracks")
    suspend fun deleteStarredTracks()

    @Query("DELETE FROM starred_albums")
    suspend fun deleteStarredAlbums()

    @Query("DELETE FROM album_tracks")
    suspend fun deleteAlbumTracks()

    @Query("DELETE FROM cover_arts")
    suspend fun deleteCoverArt()

    @Query("DELETE FROM playlists")
    suspend fun deletePlaylists()

    @Query("DELETE FROM tracks")
    suspend fun deleteTracks()

    @Query("DELETE FROM albums")
    suspend fun deleteAlbums()

    @Query("DELETE FROM artists")
    suspend fun deleteArtists()

    @Query("DELETE FROM server_configuration_custom_headers")
    suspend fun deleteServerConfigurationCustomHeaders()

    @Query("DELETE FROM server_configurations")
    suspend fun deleteServerConfigurations()
}
