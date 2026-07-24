package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE id = :albumId LIMIT 1")
    suspend fun getAlbum(albumId: String): AlbumEntity?

    @Query("SELECT * FROM albums ORDER BY created DESC")
    fun getAllAlbums(): Flow<List<AlbumEntity>>
}
