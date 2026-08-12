package fr.cassettelabs.cassette.presentation.albumDetail.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.PlayingEqIcon
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.core.track.TrackButton
import fr.cassettelabs.cassette.presentation.core.track.TrackMoreButton

@Composable
internal fun AlbumDetailTrackItem(
    modifier: Modifier = Modifier,
    track: Track,
    isCurrentTrack: Boolean = false,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    onMoreClick: () -> Unit = {},
) {
    TrackButton(
        modifier = modifier,
        track = track,
        onClick = onClick,
        leading = {
            Box(
                modifier =
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)),
                contentAlignment = Alignment.Center,
            ) {
                if (isCurrentTrack) {
                    PlayingEqIcon(
                        modifier = Modifier.size(width = 26.dp, height = 18.dp),
                        isPlaying = isPlaying,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                else {
                    Text(
                        text = track.trackNumber?.toString() ?: "-",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
        trailing = {
            TrackMoreButton(onClick = onMoreClick)
        }
    )
}

@Preview
@Composable
private fun AlbumDetailTrackItemPreview() {
    CassetteTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AlbumDetailTrackItem(
                track =
                    Track(
                        id = "",
                        title = "Hand It Over",
                        artist = "MGMT",
                        trackNumber = 1,
                        durationSeconds = 146,
                    ),
                isCurrentTrack = false,
                isPlaying = false,
                onClick = {},
            )

            AlbumDetailTrackItem(
                track =
                    Track(
                        id = "",
                        title = "Hand It Over",
                        artist = "MGMT",
                        trackNumber = 1,
                        durationSeconds = 146,
                    ),
                isCurrentTrack = true,
                isPlaying = true,
                onClick = {},
            )
        }
    }
}
