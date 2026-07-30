package fr.cassettelabs.cassette.data.remote.coverart

import android.graphics.BitmapFactory
import androidx.palette.graphics.Palette
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import java.io.File

class AndroidCoverArtProcessor(private val cacheDir: File) : CoverArtProcessor {
    override suspend fun saveCoverArt(bytes: ByteArray, cacheKey: String): String {
        val cacheFile = File(cacheDir, "${cacheKey}.img")
        if (cacheFile.exists() && cacheFile.length() > 0L) {
            return cacheFile.absolutePath
        }

        val temporaryFile = File(cacheFile.parentFile, "${cacheFile.name}.tmp")
        temporaryFile.writeBytes(bytes)
        if (!temporaryFile.renameTo(cacheFile)) {
            temporaryFile.copyTo(cacheFile, overwrite = true)
            temporaryFile.delete()
        }

        return cacheFile.absolutePath
    }

    override fun extractSeedColor(filePath: String): Int? {
        val bitmap = BitmapFactory.decodeFile(filePath) ?: return null
        val palette = Palette.from(bitmap).generate()
        val swatch =
            palette.dominantSwatch
                ?: palette.vibrantSwatch
                ?: palette.mutedSwatch
                ?: palette.darkVibrantSwatch
                ?: palette.darkMutedSwatch
                ?: return null
        return swatch.rgb
    }

    override fun fileExists(filePath: String): Boolean = File(filePath).exists()
}
