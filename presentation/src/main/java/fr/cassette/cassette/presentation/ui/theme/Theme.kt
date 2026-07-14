package fr.cassette.cassette.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
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

private val LightColorScheme = lightColorScheme(
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
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
