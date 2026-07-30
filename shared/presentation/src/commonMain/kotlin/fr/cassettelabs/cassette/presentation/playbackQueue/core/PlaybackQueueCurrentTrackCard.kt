package fr.cassettelabs.cassette.presentation.playbackQueue.core

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playback_queue_now_playing
import cassette.shared.presentation.generated.resources.playback_queue_unknown_album
import cassette.shared.presentation.generated.resources.playback_queue_unknown_artist
import cassette.shared.presentation.generated.resources.playback_queue_unknown_title
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.AlbumCoverArt
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PlaybackQueueCurrentTrackCard(
    currentTrack: Track,
    modifier: Modifier = Modifier,
) {
    val coverArtStatus = currentTrack.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
            ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                AlbumCoverArt(
                    coverArtStatus = coverArtStatus,
                    modifier = Modifier.matchParentSize(),
                )
                if (coverArtStatus == null) {
                    Icon(
                        imageVector = Icons.Rounded.Album,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.72f),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(Res.string.playback_queue_now_playing),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = currentTrack.title.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.playback_queue_unknown_title),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = currentTrack.artist?.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.playback_queue_unknown_artist),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = currentTrack.albumName?.takeIf { it.isNotBlank() }
                        ?: stringResource(Res.string.playback_queue_unknown_album),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.62f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}
