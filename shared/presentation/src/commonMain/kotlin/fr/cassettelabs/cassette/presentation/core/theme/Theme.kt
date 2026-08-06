package fr.cassettelabs.cassette.presentation.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme =
    lightColorScheme(
        primary = CassettePrimaryLight,
        onPrimary = CassetteOnPrimary,

        background = CassetteBackgroundLight,
        onBackground = CassetteOnBackgroundLight, // Default text color

        surfaceContainer = CassetteSurfaceLight,


        primaryContainer = CassetteLightAccentContainer,
        onPrimaryContainer = CassetteLightAccentForeground,
        secondary = CassetteLightTextSecondary,
        tertiary = CassetteLightTextTertiary,
        onSurface = CassetteOnBackgroundLight,
        onSurfaceVariant = CassetteLightTextSecondary,
        surfaceContainerHighest = CassetteLightBackgroundTertiary,
        outline = CassetteLightOutline,
        outlineVariant = CassetteLightSeparator,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = CassetteAccent,
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

@Composable
fun CassetteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = CassetteTypography(),
        content = content,
    )
}
