package fr.cassette.cassette.presentation.albumDetail.core

import android.graphics.Bitmap
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import kotlin.math.max
import kotlin.math.min
import android.graphics.Color as AndroidColor

@Composable
internal fun AlbumArtworkTheme(
    albumArt: Bitmap?,
    content: @Composable () -> Unit,
) {
    val baseColorScheme = MaterialTheme.colorScheme
    val albumColorScheme =
        remember(baseColorScheme, albumArt) {
            albumArt?.let { buildAlbumColorScheme(baseColorScheme, it) } ?: baseColorScheme
        }

    MaterialTheme(
        colorScheme = albumColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
    )
}

private fun buildAlbumColorScheme(
    baseColorScheme: ColorScheme,
    bitmap: Bitmap,
): ColorScheme {
    val seed = extractSeedColor(bitmap) ?: return baseColorScheme
    val seedArgb = seed.toArgb()
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seedArgb, hsl)
    hsl[1] = (hsl[1] * 1.18f).coerceIn(0.30f, 0.82f)
    hsl[2] = hsl[2].coerceIn(0.32f, 0.56f)
    val tunedSeed = Color(ColorUtils.HSLToColor(hsl))

    return if (baseColorScheme.background.isDark()) {
        buildDarkAlbumColorScheme(baseColorScheme, tunedSeed)
    } else {
        buildLightAlbumColorScheme(baseColorScheme, tunedSeed)
    }
}

private fun buildDarkAlbumColorScheme(
    baseColorScheme: ColorScheme,
    seed: Color,
): ColorScheme {
    val background = lerp(seed, Color.Black, 0.74f)
    val surfaceContainer = lerp(background, Color.White, 0.10f)
    val surfaceContainerHigh = lerp(background, Color.White, 0.14f)
    val surfaceContainerHighest = lerp(background, Color.White, 0.20f)
    val primaryContainer = lerp(seed, Color.Black, 0.45f)
    val secondaryContainer = lerp(seed, Color.Black, 0.18f)
    val primary = buildAccentFromSeed(seed, lightnessDelta = 0.08f)

    return baseColorScheme.copy(
        primary = primary,
        onPrimary = bestContrastContent(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = bestContrastContent(primaryContainer),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = bestContrastContent(secondaryContainer),
        background = background,
        onBackground = Color(0xFFF7F2FF),
        surface = background,
        onSurface = Color(0xFFF7F2FF),
        onSurfaceVariant = Color(0xFFE8DEF8),
        surfaceContainer = surfaceContainer.copy(alpha = 0.95f),
        surfaceContainerHigh = surfaceContainerHigh.copy(alpha = 0.97f),
        surfaceContainerHighest = surfaceContainerHighest.copy(alpha = 0.98f),
    )
}

private fun buildLightAlbumColorScheme(
    baseColorScheme: ColorScheme,
    seed: Color,
): ColorScheme {
    val background = lerp(seed, Color.White, 0.80f)
    val surfaceContainer = lerp(seed, Color.White, 0.68f)
    val surfaceContainerHigh = lerp(seed, Color.White, 0.62f)
    val surfaceContainerHighest = lerp(seed, Color.White, 0.56f)
    val primaryContainer = lerp(seed, Color.White, 0.50f)
    val secondaryContainer = lerp(seed, Color.White, 0.62f)
    val primary = buildAccentFromSeed(seed, lightnessDelta = -0.12f)

    return baseColorScheme.copy(
        primary = primary,
        onPrimary = bestContrastContent(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = bestContrastContent(primaryContainer),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = bestContrastContent(secondaryContainer),
        background = background,
        onBackground = Color(0xFF17141E),
        surface = background,
        onSurface = Color(0xFF17141E),
        onSurfaceVariant = Color(0xFF4F4656),
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
    )
}

private fun buildAccentFromSeed(
    seed: Color,
    lightnessDelta: Float,
): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seed.toArgb(), hsl)
    hsl[1] = (hsl[1] * 1.22f).coerceIn(0.46f, 0.92f)
    hsl[2] = (hsl[2] + lightnessDelta).coerceIn(0.34f, 0.72f)
    return Color(ColorUtils.HSLToColor(hsl))
}

private fun extractSeedColor(bitmap: Bitmap): Color? {
    val readableBitmap = bitmap.readableCopy() ?: return null
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
            val pixel = readableBitmap.getPixel(x, y)
            val alpha = AndroidColor.alpha(pixel)
            if (alpha >= 28) {
                val red = AndroidColor.red(pixel)
                val green = AndroidColor.green(pixel)
                val blue = AndroidColor.blue(pixel)
                if (red + green + blue > 36) {
                    redSum += red
                    greenSum += green
                    blueSum += blue
                    count++
                }
            }
            x += step
        }
        y += step
    }

    if (count == 0L) return null
    return Color(
        AndroidColor.rgb(
            (redSum / count).toInt(),
            (greenSum / count).toInt(),
            (blueSum / count).toInt(),
        ),
    )
}

private fun Bitmap.readableCopy(): Bitmap? =
    when (config) {
        Bitmap.Config.HARDWARE -> copy(Bitmap.Config.ARGB_8888, false)
        else -> this
    }

private fun bestContrastContent(background: Color): Color {
    val light = Color(0xFFF6F2FF)
    val dark = Color(0xFF17141E)
    val backgroundArgb = background.toArgb()
    val lightContrast = ColorUtils.calculateContrast(light.toArgb(), backgroundArgb)
    val darkContrast = ColorUtils.calculateContrast(dark.toArgb(), backgroundArgb)
    return if (lightContrast >= darkContrast) light else dark
}

private fun Color.isDark(): Boolean = ColorUtils.calculateLuminance(toArgb()) < 0.5
