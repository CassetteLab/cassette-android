package fr.cassettelabs.cassette.presentation.albumDetail.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_detail_menu
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.PlayingEqIcon
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AlbumDetailTrackItem(
    modifier: Modifier = Modifier,
    track: Track,
    isCurrentTrack: Boolean = false,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable(onClick = onClick)
                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.76f))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f, fill = false),
                    text = track.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            track.artist?.let { artist ->
                Text(
                    text = artist,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        FilledIconButton(
            onClick = { },
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            modifier =
                Modifier
                    .size(36.dp)
                    .padding(end = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(Res.string.playlist_detail_menu),
            )
        }
    }
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
