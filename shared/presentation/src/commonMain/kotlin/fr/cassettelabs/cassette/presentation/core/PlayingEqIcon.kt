package fr.cassettelabs.cassette.presentation.core

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.sin

@Composable
internal fun PlayingEqIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    isPlaying: Boolean = true,
    bars: Int = 3,
    minHeightFraction: Float = 0.28f,
    maxHeightFraction: Float = 1f,
    phaseDurationMillis: Int = 2_400,
    wanderDurationMillis: Int = 8_000,
    gapFraction: Float = 0.30f,
) {
    val fullRotation = (2f * PI).toFloat()
    val phaseAnim = remember { Animatable(0f) }
    val wanderAnim = remember { Animatable(0f) }
    val animate = isPlaying

    LaunchedEffect(animate, phaseDurationMillis) {
        if (!animate) return@LaunchedEffect
        while (isActive) {
            val start = (phaseAnim.value % fullRotation).let { if (it < 0f) it + fullRotation else it }
            phaseAnim.snapTo(start)
            phaseAnim.animateTo(
                targetValue = start + fullRotation,
                animationSpec = tween(durationMillis = phaseDurationMillis, easing = LinearEasing),
            )
        }
    }

    LaunchedEffect(animate, wanderDurationMillis) {
        if (!animate) return@LaunchedEffect
        while (isActive) {
            val start = (wanderAnim.value % fullRotation).let { if (it < 0f) it + fullRotation else it }
            wanderAnim.snapTo(start)
            wanderAnim.animateTo(
                targetValue = start + fullRotation,
                animationSpec = tween(durationMillis = wanderDurationMillis, easing = LinearEasing),
            )
        }
    }

    val activity by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "playingEqActivity",
    )
    val speeds = remember(bars) { List(bars) { (it + 1).toFloat() } }
    val shifts = remember(bars) { List(bars) { index -> index * 0.9f } }

    Canvas(modifier = modifier) {
        val tentativeBarWidth = size.width / (bars + (bars - 1) * (1f + gapFraction))
        val gap = tentativeBarWidth * gapFraction
        val corner = CornerRadius(tentativeBarWidth / 2f, tentativeBarWidth / 2f)

        repeat(bars) { index ->
            val slowShift = 0.6f * sin(wanderAnim.value + index * 0.4f)
            val slowAmplitude = 0.85f + 0.15f * sin(wanderAnim.value * 0.5f + 1.1f + index * 0.3f)
            val waveform = (sin(phaseAnim.value * speeds[index] + shifts[index] + slowShift) * slowAmplitude + 1f) * 0.5f
            val eased = waveform * waveform * (3 - 2 * waveform)
            val barHeightFraction = minHeightFraction + (maxHeightFraction - minHeightFraction) * eased
            val barHeight = size.height * barHeightFraction
            val dotHeight = tentativeBarWidth
            val blendedHeight = dotHeight + (barHeight - dotHeight) * activity

            drawRoundRect(
                color = color,
                topLeft = Offset(x = index * (tentativeBarWidth + gap), y = (size.height - blendedHeight) / 2f),
                size = Size(width = tentativeBarWidth, height = blendedHeight),
                cornerRadius = corner,
            )
        }
    }
}

@Preview
@Composable
private fun PlayingEqIconPreview() {
    CassetteTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PlayingEqIcon(
                modifier = Modifier.size(width = 18.dp, height = 16.dp),
            )
        }
    }
}
