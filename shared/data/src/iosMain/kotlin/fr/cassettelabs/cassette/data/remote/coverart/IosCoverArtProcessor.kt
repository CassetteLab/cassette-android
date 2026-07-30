package fr.cassettelabs.cassette.data.remote.coverart

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.create
import platform.Foundation.writeToFile
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

@OptIn(ExperimentalForeignApi::class)
class IosCoverArtProcessor : CoverArtProcessor {
    private val cacheDir: String by lazy {
        val cachesPath = NSHomeDirectory() + "/Library/Caches/CoverArt"
        NSFileManager.defaultManager.createDirectoryAtPath(cachesPath, true, null, null)
        cachesPath
    }

    override suspend fun saveCoverArt(bytes: ByteArray, cacheKey: String): String {
        val filePath = "$cacheDir/${cacheKey}.img"
        if (NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
            return filePath
        }

        val data = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        data.writeToFile(filePath, false)

        return filePath
    }

    override fun extractSeedColor(filePath: String): Int? = null

    override fun fileExists(filePath: String): Boolean = NSFileManager.defaultManager.fileExistsAtPath(filePath)
}
