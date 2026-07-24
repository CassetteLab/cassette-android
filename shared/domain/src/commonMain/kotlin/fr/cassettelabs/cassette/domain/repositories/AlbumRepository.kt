package fr.cassettelabs.cassette.domain.repositories

import fr.cassettelabs.cassette.domain.models.AlbumList
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAllAlbums(): Flow<List<AlbumList>>

    suspend fun refreshAlbums()
}
