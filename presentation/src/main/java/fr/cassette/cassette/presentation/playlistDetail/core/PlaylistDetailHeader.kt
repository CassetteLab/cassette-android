package fr.cassette.cassette.presentation.playlistDetail.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.PlaylistDetail
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.AlbumCoverArt
import fr.cassette.cassette.presentation.core.ShuffleButton
import fr.cassette.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel

@Composable
internal fun PlaylistDetailHeader(
    playlist: PlaylistDetail?,
    coverArt: AlbumCoverArtModel?,
    tracksCount: Int,
    height: Dp,
    onShuffleClick: () -> Unit,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height),
    ) {
        AlbumCoverArt(
            coverArt = coverArt,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    surfaceColor.copy(alpha = 0.28f),
                                    surfaceColor.copy(alpha = 0.88f),
                                    surfaceColor,
                                ),
                        ),
                    ),
        )
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 28.dp, start = 12.dp, end = 12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = playlist?.name ?: stringResource(R.string.playlist_detail_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = pluralStringResource(R.plurals.playlist_detail_tracks_count, tracksCount, tracksCount),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            ShuffleButton(onClick = onShuffleClick)
        }
    }
}
