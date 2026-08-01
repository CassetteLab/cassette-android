package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.embeddeds.ArtistWithCoverArt
import fr.cassettelabs.cassette.data.local.entities.ArtistEntity

@Dao
internal interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE id = :artistId LIMIT 1")
    suspend fun getArtist(artistId: String): ArtistEntity?

    @Query(
        """
        SELECT artists.*, cover_arts.filePath AS coverArtFilePath
        FROM artists
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = artists.serverConfigurationId
            AND cover_arts.coverArtId = artists.coverArt
            AND cover_arts.size = :coverArtSize
        WHERE artists.id = :artistId
        LIMIT 1
        """,
    )
    suspend fun getArtistWithCoverArt(
        artistId: String,
        coverArtSize: Int,
    ): ArtistWithCoverArt?
}
