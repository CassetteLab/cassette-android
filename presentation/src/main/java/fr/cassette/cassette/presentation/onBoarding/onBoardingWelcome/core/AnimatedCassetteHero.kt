package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.core

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
internal fun AnimatedCassetteHero(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val reelAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_500, easing = LinearEasing),
        ),
    )
    val accent = MaterialTheme.colorScheme.primary
    val accentContainer = MaterialTheme.colorScheme.primaryContainer
    val cassetteBody = MaterialTheme.colorScheme.surfaceContainerHighest
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier.size(290.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = accent.copy(alpha = 0.08f),
                radius = size.minDimension / 2f,
                center = center,
            )

            val cassetteWidth = 200.dp.toPx()
            val cassetteHeight = 130.dp.toPx()
            val left = (size.width - cassetteWidth) / 2f
            val top = (size.height - cassetteHeight) / 2f
            val reelRadius = cassetteWidth * 0.16f
            val reelY = top + cassetteHeight * 0.40f + reelRadius / 2f
            val leftReel = Offset(left + cassetteWidth * 0.285f, reelY)
            val rightReel = Offset(left + cassetteWidth * 0.715f, reelY)

            drawReel(
                center = leftReel,
                radius = reelRadius,
                angle = reelAngle,
                background = background,
                accent = accent,
                accentContainer = accentContainer,
            )
            drawReel(
                center = rightReel,
                radius = reelRadius,
                angle = reelAngle,
                background = background,
                accent = accent,
                accentContainer = accentContainer,
            )

            val bodyPath = cassettePath(
                left = left,
                top = top,
                width = cassetteWidth,
                height = cassetteHeight,
            )
            drawPath(
                path = bodyPath,
                color = cassetteBody,
            )
            drawPath(
                path = bodyPath,
                color = accent.copy(alpha = 0.65f),
                style = Stroke(width = 1.5.dp.toPx()),
            )

            val windowTop = top + cassetteHeight * 0.24f
            drawRoundRect(
                color = background.copy(alpha = 0.88f),
                topLeft = Offset(left + cassetteWidth * 0.18f, windowTop),
                size = Size(cassetteWidth * 0.64f, cassetteHeight * 0.34f),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
            )
            drawRoundRect(
                color = accent.copy(alpha = 0.32f),
                topLeft = Offset(left + cassetteWidth * 0.25f, top + cassetteHeight * 0.72f),
                size = Size(cassetteWidth * 0.50f, 10.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
            )
        }
    }
}

private fun cassettePath(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
): Path = Path().apply {
    val corner = width * 0.08f
    val notch = height * 0.16f
    addRoundRect(
        roundRect = RoundRect(
            rect = Rect(left, top, left + width, top + height),
            cornerRadius = CornerRadius(corner, corner),
        ),
    )
    moveTo(left + width * 0.25f, top + height)
    lineTo(left + width * 0.33f, top + height - notch)
    lineTo(left + width * 0.67f, top + height - notch)
    lineTo(left + width * 0.75f, top + height)
    close()
}

private fun DrawScope.drawReel(
    center: Offset,
    radius: Float,
    angle: Float,
    background: Color,
    accent: Color,
    accentContainer: Color,
) {
    drawCircle(
        color = background,
        radius = radius,
        center = center,
    )
    drawCircle(
        color = accent.copy(alpha = 0.4f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.dp.toPx()),
    )
    drawCircle(
        color = accentContainer,
        radius = radius * 0.225f,
        center = center,
    )
    rotate(degrees = angle, pivot = center) {
        repeat(3) { index ->
            rotate(degrees = index * 120f, pivot = center) {
                drawLine(
                    color = accent.copy(alpha = 0.5f),
                    start = center.copy(y = center.y - radius * 0.14f),
                    end = center.copy(y = center.y - radius * 0.76f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
