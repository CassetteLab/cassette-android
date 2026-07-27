package fr.cassettelabs.cassette.presentation.albumDetail.core

import org.jetbrains.skia.Bitmap
import kotlin.math.max
import kotlin.math.min

internal actual fun extractAlbumArtworkSeedColor(bitmap: Bitmap): Int? {
    val width = bitmap.width
    val height = bitmap.height
    if (width <= 0 || height <= 0) return null

    val step = max(1, min(width, height) / 24)
    var redSum = 0L
    var greenSum = 0L
    var blueSum = 0L
    var count = 0L

    var y = 0
    while (y < height) {
        var x = 0
        while (x < width) {
            val pixel = bitmap.getColor(x, y)
            val alpha = (pixel ushr 24) and 0xFF
            val red = (pixel ushr 16) and 0xFF
            val green = (pixel ushr 8) and 0xFF
            val blue = pixel and 0xFF
            if (alpha >= 28 && red + green + blue > 36) {
                redSum += red
                greenSum += green
                blueSum += blue
                count++
            }
            x += step
        }
        y += step
    }

    if (count == 0L) return null
    return (0xFF shl 24) or
        ((redSum / count).toInt() shl 16) or
        ((greenSum / count).toInt() shl 8) or
        (blueSum / count).toInt()
}
