package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.embeddeds.AlbumWithCoverArt
import fr.cassettelabs.cassette.data.local.entities.AlbumEntity
import fr.cassettelabs.cassette.data.local.entities.StarredAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE id = :albumId LIMIT 1")
    suspend fun getAlbum(albumId: String): AlbumEntity?

    @Query(
        """
        SELECT albums.*, cover_arts.filePath AS coverArtFilePath
        FROM albums
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = albums.serverConfigurationId
            AND cover_arts.coverArtId = albums.coverArt
            AND cover_arts.size = :coverArtSize
        WHERE albums.id = :albumId
        LIMIT 1
        """,
    )
    suspend fun getAlbumWithCoverArt(
        albumId: String,
        coverArtSize: Int,
    ): AlbumWithCoverArt?

    @Query(
        """
        SELECT albums.*, cover_arts.filePath AS coverArtFilePath
        FROM albums
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = albums.serverConfigurationId
            AND cover_arts.coverArtId = albums.coverArt
            AND cover_arts.size = :coverArtSize
        ORDER BY albums.created DESC
        """,
    )
    fun getAllAlbums(coverArtSize: Int): Flow<List<AlbumWithCoverArt>>

    @Query(
        """
        SELECT albums.*, cover_arts.filePath AS coverArtFilePath
        FROM albums
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = albums.serverConfigurationId
            AND cover_arts.coverArtId = albums.coverArt
            AND cover_arts.size = :coverArtSize
        WHERE albums.artistId = :artistId
        ORDER BY albums.created DESC, albums.name
        """,
    )
    suspend fun getArtistAlbums(
        artistId: String,
        coverArtSize: Int,
    ): List<AlbumWithCoverArt>

    @Query("DELETE FROM albums")
    suspend fun deleteAllAlbums()

    @Query("UPDATE albums SET seedColor = :seedColor WHERE id = :albumId")
    suspend fun updateSeedColor(
        albumId: String,
        seedColor: Int,
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStarredAlbum(starredAlbum: StarredAlbumEntity)

    @Query("DELETE FROM starred_albums WHERE albumId = :albumId AND starredAt IS NULL")
    suspend fun deleteUnstarredAlbum(albumId: String)
}
