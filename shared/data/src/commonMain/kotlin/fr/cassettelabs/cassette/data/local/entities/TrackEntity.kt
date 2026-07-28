package fr.cassettelabs.cassette.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    indices = [Index("albumId")],
)
internal data class TrackEntity(
    @PrimaryKey
    val id: String,
    val albumId: String? = null,
    val title: String,
    val artist: String? = null,
    val trackNumber: Int? = null,
    val durationSeconds: Int? = null,
    val albumName: String? = null,
    val coverArt: String? = null,
    val coverArtFilePath: String? = null,
    val starredAt: String? = null,
)
