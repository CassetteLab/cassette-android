package fr.cassette.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassette.cassette.data.local.entities.AlbumEntity

@Dao
internal interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE id = :albumId LIMIT 1")
    suspend fun getAlbum(albumId: String): AlbumEntity?

    @Query("UPDATE albums SET coverArtFilePath = :coverArtFilePath WHERE id = :albumId")
    suspend fun updateCoverArtFilePath(albumId: String, coverArtFilePath: String)
}
