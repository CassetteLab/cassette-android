package fr.cassettelabs.cassette.presentation.nowPlaying.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun PlayerControlButton(
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(64.dp),
    content: @Composable () -> Unit,
) {
    IconButton(
        modifier =
            modifier
                .clip(CircleShape)
                .background(containerColor),
        onClick = onClick,
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
            Box(contentAlignment = Alignment.Center) {
                CompositionLocalProvider(
                    LocalContentColor provides contentColor,
                    content = content,
                )
            }
        }
    }
}
