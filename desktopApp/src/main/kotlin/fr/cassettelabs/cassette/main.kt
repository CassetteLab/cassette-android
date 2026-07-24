package fr.cassettelabs.cassette

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    var startupConfiguration by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        startupConfiguration = hasValidServerConfiguration()
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Cassette",
    ) {
        val hasValidServerConfiguration = startupConfiguration
        if (hasValidServerConfiguration == null) {
            DesktopSplashScreen()
        } else {
            App(hasValidServerConfiguration = hasValidServerConfiguration)
        }
    }
}

@Composable
private fun DesktopSplashScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF101014)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val accent = Color(0xFFFFB15C)
            val body = Color(0xFF26242B)
            val width = size.width * 0.78f
            val height = size.height * 0.48f
            val left = (size.width - width) / 2f
            val top = (size.height - height) / 2f

            drawCircle(color = accent.copy(alpha = 0.10f), radius = size.minDimension / 2f)
            drawRoundRect(
                color = body,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(width, height),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            )
            drawRoundRect(
                color = accent.copy(alpha = 0.7f),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(width, height),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx()),
            )
            drawCircle(color = Color(0xFF101014), radius = width * 0.12f, center = Offset(left + width * 0.30f, top + height * 0.42f))
            drawCircle(color = Color(0xFF101014), radius = width * 0.12f, center = Offset(left + width * 0.70f, top + height * 0.42f))
            drawPath(
                path =
                    Path().apply {
                        moveTo(left + width * 0.28f, top + height)
                        lineTo(left + width * 0.36f, top + height * 0.82f)
                        lineTo(left + width * 0.64f, top + height * 0.82f)
                        lineTo(left + width * 0.72f, top + height)
                        close()
                    },
                color = Color(0xFF101014).copy(alpha = 0.55f),
            )
        }
    }
}
