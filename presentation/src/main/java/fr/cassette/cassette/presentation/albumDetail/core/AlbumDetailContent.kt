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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.core.AlbumCoverArt

@Composable
internal fun AlbumDetailContent(
    album: AlbumDetail,
    coverArt: AlbumCoverArtModel?,
    onTrackClick: (Track) -> Unit,
) {
    val panelColor = MaterialTheme.colorScheme.primaryContainer

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
                    coverArt = coverArt,
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
        }

        album.tracks.forEach { track ->
            AlbumDetailTrackRow(
                track = track,
                onClick = { onTrackClick(track) },
            )
        }
    }
}
