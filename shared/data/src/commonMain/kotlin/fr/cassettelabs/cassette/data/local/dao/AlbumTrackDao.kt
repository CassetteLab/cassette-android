package fr.cassettelabs.cassette.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.cassettelabs.cassette.data.local.embeddeds.AlbumTrackWithTrack
import fr.cassettelabs.cassette.data.local.entities.AlbumTrackEntity

@Dao
internal interface AlbumTrackDao {
    @Query(
        """
        SELECT
            tracks.id AS trackId,
            tracks.title AS title,
            tracks.artist AS artist,
            tracks.artistId AS artistId,
            album_tracks.trackNumber AS trackNumber,
            tracks.durationSeconds AS durationSeconds,
            album_tracks.albumId AS albumId,
            COALESCE(albums.name, tracks.albumName) AS albumName,
            COALESCE(albums.coverArt, tracks.coverArt) AS coverArt,
            cover_arts.filePath AS coverArtFilePath,
            tracks.starredAt AS starredAt
        FROM album_tracks
        INNER JOIN tracks ON tracks.id = album_tracks.trackId
        LEFT JOIN albums ON albums.id = album_tracks.albumId
        LEFT JOIN cover_arts ON cover_arts.serverConfigurationId = albums.serverConfigurationId
            AND cover_arts.coverArtId = COALESCE(albums.coverArt, tracks.coverArt)
            AND cover_arts.size = :coverArtSize
        WHERE album_tracks.albumId = :albumId
        ORDER BY album_tracks.trackNumber, tracks.title
        """,
    )
    suspend fun getAlbumTracks(
        albumId: String,
        coverArtSize: Int,
    ): List<AlbumTrackWithTrack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumTracks(tracks: List<AlbumTrackEntity>)

    @Query("DELETE FROM album_tracks WHERE albumId = :albumId")
    suspend fun deleteAlbumTracks(albumId: String)

    @Transaction
    suspend fun replaceAlbumTracks(
        albumId: String,
        tracks: List<AlbumTrackEntity>,
    ) {
        deleteAlbumTracks(albumId)
        insertAlbumTracks(tracks)
    }
}
