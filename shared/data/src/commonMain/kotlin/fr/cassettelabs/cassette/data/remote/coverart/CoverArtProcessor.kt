package fr.cassettelabs.cassette.data.remote.coverart

interface CoverArtProcessor {
    suspend fun saveCoverArt(bytes: ByteArray, cacheKey: String): String

    fun extractSeedColor(filePath: String): Int?

    fun fileExists(filePath: String): Boolean

    fun clearCache()
}
