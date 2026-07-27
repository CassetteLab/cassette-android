package fr.cassettelabs.cassette.presentation.core

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

internal data class AlbumArtColors(
    val backgroundColor: Color,
    val textColor: Color,
)

internal fun seedColorToAlbumArtColors(seedColor: Int): AlbumArtColors {
    val bgColor = Color(seedColor)
    val textLuminance = bgColor.luminance()
    val onBgColor = if (textLuminance > 0.5f) Color.Black else Color.White
    return AlbumArtColors(
        backgroundColor = bgColor,
        textColor = onBgColor,
    )
}
