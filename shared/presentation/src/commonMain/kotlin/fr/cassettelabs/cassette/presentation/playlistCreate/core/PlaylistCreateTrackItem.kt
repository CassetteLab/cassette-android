package fr.cassettelabs.cassette.presentation.playlistCreate.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.CoverArtLoading
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun PlaylistCreateTrackItem(
    track: Track,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    coverArtStatus: CoverArtLoadingStatus? = track.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors =
                CheckboxDefaults.colors(
                    checkedCheckmarkColor = MaterialTheme.colorScheme.onPrimary,
                    checkedBoxColor = MaterialTheme.colorScheme.primary,
                ),
        )

        Box(
            modifier =
                Modifier
                    .padding(start = 4.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            val loadedCoverArtPath = (coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: track.coverArtFilePath
            if (loadedCoverArtPath != null) {
                AsyncImage(
                    model = loadedCoverArtPath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else if (coverArtStatus == CoverArtLoadingStatus.Loading) {
                CoverArtLoading()
            } else {
                Icon(
                    imageVector = Icons.Rounded.Album,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.48f),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = track.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
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

        track.durationSeconds?.let { durationSeconds ->
            Text(
                text = formatDuration(durationSeconds),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

private fun formatDuration(durationSeconds: Int): String {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
@PreviewLightDark
private fun PlaylistCreateTrackItemPreview() {
    CassetteTheme {
        PlaylistCreateTrackItem(
            track =
                Track(
                    id = "track-1",
                    title = "One More Time",
                    artist = "Daft Punk",
                    trackNumber = 1,
                    durationSeconds = 320,
                ),
            isSelected = true,
            onToggle = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun PlaylistCreateTrackItemUnselectedPreview() {
    CassetteTheme {
        PlaylistCreateTrackItem(
            track =
                Track(
                    id = "track-2",
                    title = "Aerodynamic",
                    artist = "Daft Punk",
                    trackNumber = 2,
                    durationSeconds = 212,
                ),
            isSelected = false,
            onToggle = {},
        )
    }
}
