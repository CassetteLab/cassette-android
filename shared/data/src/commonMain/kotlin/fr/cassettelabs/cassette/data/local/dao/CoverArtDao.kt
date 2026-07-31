package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.cassettelabs.cassette.data.local.entities.CoverArtEntity

@Dao
internal interface CoverArtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoverArt(coverArt: CoverArtEntity)

    @Query("SELECT * FROM cover_arts WHERE serverConfigurationId = :serverConfigurationId AND coverArtId = :coverArtId AND size = :size LIMIT 1")
    suspend fun getCoverArt(
        serverConfigurationId: Long,
        coverArtId: String,
        size: Int,
    ): CoverArtEntity?
}
