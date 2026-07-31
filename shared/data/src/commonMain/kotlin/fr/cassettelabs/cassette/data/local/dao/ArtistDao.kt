package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.entities.ArtistEntity

@Dao
internal interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE id = :artistId LIMIT 1")
    suspend fun getArtist(artistId: String): ArtistEntity?

    @Query("UPDATE artists SET coverArtFilePath = :coverArtFilePath WHERE id = :artistId")
    suspend fun updateCoverArtFilePath(
        artistId: String,
        coverArtFilePath: String,
    )
}
