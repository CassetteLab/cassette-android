package fr.cassette.cassette.presentation.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun NowPlayingSnack(
    track: String,
    artist: String,
    onPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit
) {
    Button(
        shape = CircleShape,
        contentPadding = PaddingValues(12.dp),
        onClick = onExpand,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .size(44.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(track)
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        IconButton(
            onClick = onPause,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = null
            )
        }

        IconButton(
            onClick = onNext,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Icon(
                imageVector = Icons.Filled.SkipNext,
                contentDescription = null
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun NowPlayingSnackPreview(){
    CassetteTheme {
        NowPlayingSnack(
            track = "Electric Feel",
            artist = "MGMT",
            onPause = { },
            onNext = { },
            onExpand = { }
        )
    }
}