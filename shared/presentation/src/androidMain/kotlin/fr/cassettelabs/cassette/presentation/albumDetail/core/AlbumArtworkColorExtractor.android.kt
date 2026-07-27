package fr.cassettelabs.cassette.presentation.albumDetail.core

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

internal actual fun extractAlbumArtworkSeedColor(bitmap: Bitmap): Int? {
    val readableBitmap = bitmap.readableCopy() ?: return null
    val width = readableBitmap.width
    val height = readableBitmap.height
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
            val pixel = readableBitmap.getPixel(x, y)
            val alpha = Color.alpha(pixel)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
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
    return Color.rgb(
        (redSum / count).toInt(),
        (greenSum / count).toInt(),
        (blueSum / count).toInt(),
    )
}

private fun Bitmap.readableCopy(): Bitmap? =
    when (config) {
        Bitmap.Config.HARDWARE -> copy(Bitmap.Config.ARGB_8888, false)
        else -> this
    }
