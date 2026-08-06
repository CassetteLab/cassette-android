package fr.cassettelabs.cassette.data.remote.coverart

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor

class DesktopCoverArtProcessor : CoverArtProcessor {
    override suspend fun saveCoverArt(bytes: ByteArray, cacheKey: String): String {
        val cacheDir = File(System.getProperty("user.home"), ".cassette/cover_art")
        cacheDir.mkdirs()
        val cacheFile = File(cacheDir, "${cacheKey}.img")
        if (cacheFile.exists() && cacheFile.length() > 0L) {
            return cacheFile.absolutePath
        }

        cacheFile.writeBytes(bytes)
        return cacheFile.absolutePath
    }

    override fun extractSeedColor(filePath: String): Int? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val image: BufferedImage = ImageIO.read(file) ?: return null
            val color = getDominantColor(image)
            color
        } catch (_: Exception) {
            null
        }
    }

    override fun fileExists(filePath: String): Boolean = File(filePath).exists()

    override fun clearCache() {
        File(System.getProperty("user.home"), ".cassette/cover_art").deleteRecursively()
    }

    private fun getDominantColor(image: BufferedImage): Int {
        val colorCounts = mutableMapOf<Int, Int>()
        val stepX = maxOf(1, image.width / 10)
        val stepY = maxOf(1, image.height / 10)

        for (y in 0 until image.height step stepY) {
            for (x in 0 until image.width step stepX) {
                val rgb = image.getRGB(x, y)
                val simplified = (rgb and 0xF0F0F0.toInt())
                colorCounts[simplified] = (colorCounts[simplified] ?: 0) + 1
            }
        }

        return colorCounts.maxByOrNull { it.value }?.key ?: 0
    }
}
