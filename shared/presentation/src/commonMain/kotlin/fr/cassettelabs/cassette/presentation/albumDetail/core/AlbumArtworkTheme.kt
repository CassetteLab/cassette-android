package fr.cassettelabs.cassette.presentation.albumDetail.core

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import coil3.Bitmap
import coil3.SingletonImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun AlbumArtworkTheme(
    albumArt: String?,
    content: @Composable () -> Unit,
) {
    val platformContext = LocalPlatformContext.current
    val imageLoader = SingletonImageLoader.get(platformContext)
    MaterialTheme(
        colorScheme = rememberAlbumColorScheme(
            albumArt = albumArt,
            imageLoader = imageLoader,
            platformContext = platformContext,
            baseColorScheme = MaterialTheme.colorScheme,
        ),
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
    )
}

@Composable
private fun rememberAlbumColorScheme(
    albumArt: String?,
    imageLoader: coil3.ImageLoader,
    platformContext: coil3.PlatformContext,
    baseColorScheme: ColorScheme,
): ColorScheme {
    var seedColor by remember(albumArt) { mutableStateOf<Int?>(null) }

    LaunchedEffect(albumArt, imageLoader, platformContext) {
        seedColor = null
        if (albumArt == null) return@LaunchedEffect

        val request =
            ImageRequest
                .Builder(platformContext)
                .data(albumArt)
                .build()
        val result = imageLoader.execute(request)
        seedColor =
            (result as? SuccessResult)
                ?.image
                ?.toBitmap()
                ?.let(::extractAlbumArtworkSeedColor)
    }

    return remember(baseColorScheme, seedColor) {
        seedColor?.let { buildAlbumColorScheme(baseColorScheme, Color(it)) } ?: baseColorScheme
    }
}

private fun buildAlbumColorScheme(
    baseColorScheme: ColorScheme,
    seed: Color,
): ColorScheme {
    val hsl = seed.toHsl()
    hsl[1] = (hsl[1] * 1.18f).coerceIn(0.30f, 0.82f)
    hsl[2] = hsl[2].coerceIn(0.32f, 0.56f)
    val tunedSeed = colorFromHsl(hsl)

    return if (baseColorScheme.background.luminance() < 0.5f) {
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
    val secondary = buildSecondaryFromSeed(seed, isDark = true)
    val tertiary = buildTertiaryFromSeed(seed, isDark = true)
    val tertiaryContainer = lerp(tertiary, Color.Black, 0.45f)

    return baseColorScheme.copy(
        primary = primary,
        onPrimary = bestContrastContent(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = bestContrastContent(primaryContainer),
        secondary = secondary,
        onSecondary = bestContrastContent(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = bestContrastContent(secondaryContainer),
        tertiary = tertiary,
        onTertiary = bestContrastContent(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = bestContrastContent(tertiaryContainer),
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
    val secondary = buildSecondaryFromSeed(seed, isDark = false)
    val tertiary = buildTertiaryFromSeed(seed, isDark = false)
    val tertiaryContainer = lerp(tertiary, Color.White, 0.50f)

    return baseColorScheme.copy(
        primary = primary,
        onPrimary = bestContrastContent(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = bestContrastContent(primaryContainer),
        secondary = secondary,
        onSecondary = bestContrastContent(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = bestContrastContent(secondaryContainer),
        tertiary = tertiary,
        onTertiary = bestContrastContent(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = bestContrastContent(tertiaryContainer),
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
    val hsl = seed.toHsl()
    hsl[1] = (hsl[1] * 1.22f).coerceIn(0.46f, 0.92f)
    hsl[2] = (hsl[2] + lightnessDelta).coerceIn(0.34f, 0.72f)
    return colorFromHsl(hsl)
}

private fun buildSecondaryFromSeed(
    seed: Color,
    isDark: Boolean,
): Color {
    val hsl = seed.toHsl()
    hsl[1] = (hsl[1] * 0.60f).coerceIn(0.12f, 0.38f)
    hsl[2] = if (isDark) 0.48f else 0.44f
    return colorFromHsl(hsl)
}

private fun buildTertiaryFromSeed(
    seed: Color,
    isDark: Boolean,
): Color {
    val hsl = seed.toHsl()
    hsl[0] = (hsl[0] + 60f) % 360f
    hsl[1] = (hsl[1] * 0.90f).coerceIn(0.32f, 0.76f)
    hsl[2] = if (isDark) 0.52f else 0.44f
    return colorFromHsl(hsl)
}

private fun bestContrastContent(background: Color): Color {
    val light = Color(0xFFF6F2FF)
    val dark = Color(0xFF17141E)
    val lightContrast = contrastRatio(light, background)
    val darkContrast = contrastRatio(dark, background)
    return if (lightContrast >= darkContrast) light else dark
}

private fun contrastRatio(
    foreground: Color,
    background: Color,
): Float {
    val foregroundLuminance = foreground.luminance() + 0.05f
    val backgroundLuminance = background.luminance() + 0.05f
    return max(foregroundLuminance, backgroundLuminance) / min(foregroundLuminance, backgroundLuminance)
}

private fun Color.toHsl(): FloatArray {
    val max = max(red, max(green, blue))
    val min = min(red, min(green, blue))
    val delta = max - min
    val lightness = (max + min) / 2f

    if (delta == 0f) return floatArrayOf(0f, 0f, lightness)

    val saturation = delta / (1f - kotlin.math.abs(2f * lightness - 1f))
    val hue =
        when (max) {
            red -> 60f * (((green - blue) / delta) % 6f)
            green -> 60f * (((blue - red) / delta) + 2f)
            else -> 60f * (((red - green) / delta) + 4f)
        }.let { if (it < 0f) it + 360f else it }

    return floatArrayOf(hue, saturation, lightness)
}

private fun colorFromHsl(hsl: FloatArray): Color {
    val hue = hsl[0]
    val saturation = hsl[1]
    val lightness = hsl[2]
    val chroma = (1f - kotlin.math.abs(2f * lightness - 1f)) * saturation
    val huePrime = hue / 60f
    val x = chroma * (1f - kotlin.math.abs((huePrime % 2f) - 1f))
    val match = lightness - chroma / 2f
    val (red, green, blue) =
        when {
            huePrime < 1f -> Triple(chroma, x, 0f)
            huePrime < 2f -> Triple(x, chroma, 0f)
            huePrime < 3f -> Triple(0f, chroma, x)
            huePrime < 4f -> Triple(0f, x, chroma)
            huePrime < 5f -> Triple(x, 0f, chroma)
            else -> Triple(chroma, 0f, x)
        }

    return Color(
        red = (red + match).coerceIn(0f, 1f),
        green = (green + match).coerceIn(0f, 1f),
        blue = (blue + match).coerceIn(0f, 1f),
    )
}

internal expect fun extractAlbumArtworkSeedColor(bitmap: Bitmap): Int?
