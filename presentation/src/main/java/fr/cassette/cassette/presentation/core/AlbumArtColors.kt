package fr.cassette.cassette.presentation.core

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.palette.graphics.Palette
import fr.cassette.cassette.presentation.core.theme.CassetteAccent
import fr.cassette.cassette.presentation.core.theme.CassetteAccentForeground

internal data class AlbumArtColors(
    val backgroundColor: Color,
    val textColor: Color,
)

internal fun extractAlbumArtColors(bitmap: Bitmap): AlbumArtColors {
    val palette = Palette.from(bitmap).generate()

    val swatch =
        palette.dominantSwatch
            ?: palette.vibrantSwatch
            ?: palette.mutedSwatch
            ?: palette.darkVibrantSwatch
            ?: palette.darkMutedSwatch
            ?: palette.lightVibrantSwatch
            ?: palette.lightMutedSwatch

    if (swatch != null) {
        val bgColor = Color(swatch.rgb)
        val textLuminance = bgColor.luminance()
        val onBgColor = if (textLuminance > 0.5f) Color.Black else Color.White
        return AlbumArtColors(
            backgroundColor = bgColor,
            textColor = onBgColor,
        )
    }

    return AlbumArtColors(
        backgroundColor = CassetteAccent,
        textColor = CassetteAccentForeground,
    )
}
