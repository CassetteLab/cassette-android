package fr.cassettelabs.cassette.presentation.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.space_grotesk_bold
import cassette.shared.presentation.generated.resources.space_grotesk_medium
import cassette.shared.presentation.generated.resources.space_grotesk_regular
import cassette.shared.presentation.generated.resources.space_grotesk_semibold
import org.jetbrains.compose.resources.Font

@Composable
fun CassetteTypography(): Typography {
    val fontFamily =
        FontFamily(
            Font(Res.font.space_grotesk_regular, FontWeight.Normal),
            Font(Res.font.space_grotesk_medium, FontWeight.Medium),
            Font(Res.font.space_grotesk_semibold, FontWeight.SemiBold),
            Font(Res.font.space_grotesk_bold, FontWeight.Bold),
        )

    return Typography(
        displayLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 46.sp,
                letterSpacing = (-0.5).sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                letterSpacing = (-0.2).sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.1.sp,
            ),
    )
}
