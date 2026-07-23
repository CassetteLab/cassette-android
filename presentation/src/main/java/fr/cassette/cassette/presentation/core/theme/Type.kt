package fr.cassette.cassette.presentation.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

private val fontProvider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = androidx.compose.ui.text.googlefonts.R.array.com_google_android_gms_fonts_certs,
    )

private val cassetteGoogleFont = GoogleFont("Space Grotesk")

private val CassetteFontFamily =
    FontFamily(
        Font(
            googleFont = cassetteGoogleFont,
            fontProvider = fontProvider,
            weight = FontWeight.Normal,
        ),
        Font(
            googleFont = cassetteGoogleFont,
            fontProvider = fontProvider,
            weight = FontWeight.Medium,
        ),
        Font(
            googleFont = cassetteGoogleFont,
            fontProvider = fontProvider,
            weight = FontWeight.SemiBold,
        ),
        Font(
            googleFont = cassetteGoogleFont,
            fontProvider = fontProvider,
            weight = FontWeight.Bold,
        ),
    )

val Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = CassetteFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 46.sp,
                letterSpacing = (-0.5).sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = CassetteFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.2).sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = CassetteFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = CassetteFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.1.sp,
            ),
    )
