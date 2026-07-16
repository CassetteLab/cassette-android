package fr.cassette.cassette.presentation.albumDetail.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumCoverArtRequest
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.AlbumCoverArt

@Composable
internal fun AlbumDetailContent(
    album: AlbumDetail,
    coverArtRequest: AlbumCoverArtRequest?,
    onTrackClick: (Track) -> Unit,
) {
    val panelColor = MaterialTheme.colorScheme.primaryContainer
    val panelContentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            panelColor,
                        ),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(30.dp)),
            ) {
                AlbumCoverArt(
                    coverArtRequest = coverArtRequest,
                    iconSize = 92.dp,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    panelColor.copy(alpha = 0.36f),
                                ),
                            ),
                        ),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = album.name,
                    color = panelContentColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = album.artist ?: stringResource(R.string.album_detail_unknown_artist),
                    color = panelContentColor.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.album_detail_tracks_count,
                        album.tracks.size,
                        album.tracks.size,
                    ),
                    color = panelContentColor.copy(alpha = 0.62f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        album.created?.let { created ->
            AlbumDetailMetadata(label = stringResource(R.string.album_detail_created), value = created)
        }
        if (album.tracks.isEmpty()) {
            AlbumDetailMetadata(
                label = stringResource(R.string.album_detail_tracks),
                value = stringResource(R.string.album_detail_tracks_empty),
            )
        } else {
            album.tracks.forEach { track ->
                AlbumDetailTrackRow(
                    track = track,
                    onClick = { onTrackClick(track) },
                )
            }
        }
    }
}
