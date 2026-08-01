package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
)
internal data class TrackEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val artist: String? = null,
    val artistId: String? = null,
    val durationSeconds: Int? = null,
    val albumName: String? = null,
    val coverArt: String? = null,
    val starredAt: String? = null,
)
