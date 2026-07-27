package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_albums",
    foreignKeys = [
        ForeignKey(
            entity = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal data class FavoriteAlbumEntity(
    @PrimaryKey
    val albumId: String,
    val starredAt: String? = null,
)
