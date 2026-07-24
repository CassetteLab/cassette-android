package fr.cassettelabs.cassette.presentation.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = CassetteAccent,
        onPrimary = CassetteOnAccent,
        primaryContainer = CassetteAccentContainer,
        onPrimaryContainer = CassetteAccentForeground,
        secondary = CassetteTextSecondary,
        onSecondary = CassetteBackgroundPrimary,
        tertiary = CassetteTextTertiary,
        background = CassetteBackgroundPrimary,
        onBackground = CassetteTextPrimary,
        surface = CassetteBackgroundPrimary,
        onSurface = CassetteTextPrimary,
        onSurfaceVariant = CassetteTextSecondary,
        surfaceContainer = CassetteBackgroundSecondary,
        surfaceContainerHighest = CassetteBackgroundTertiary,
        outline = CassetteOutline,
        outlineVariant = CassetteSeparator,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = CassetteLightAccent,
        onPrimary = CassetteOnAccent,
        primaryContainer = CassetteLightAccentContainer,
        onPrimaryContainer = CassetteLightAccentForeground,
        secondary = CassetteLightTextSecondary,
        onSecondary = CassetteLightBackgroundPrimary,
        tertiary = CassetteLightTextTertiary,
        background = CassetteLightBackgroundPrimary,
        onBackground = CassetteLightTextPrimary,
        surface = CassetteLightBackgroundPrimary,
        onSurface = CassetteLightTextPrimary,
        onSurfaceVariant = CassetteLightTextSecondary,
        surfaceContainer = CassetteLightBackgroundSecondary,
        surfaceContainerHighest = CassetteLightBackgroundTertiary,
        outline = CassetteLightOutline,
        outlineVariant = CassetteLightSeparator,
    )

@Composable
fun CassetteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
