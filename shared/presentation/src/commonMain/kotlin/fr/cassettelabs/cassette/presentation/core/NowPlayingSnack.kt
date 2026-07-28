package fr.cassettelabs.cassette.presentation.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun NowPlayingSnack(
    modifier: Modifier = Modifier,
    track: String,
    artist: String,
    coverArtFilePath: String?,
    coverArtStatus: CoverArtLoadingStatus? = coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onExpand: () -> Unit,
) {
    AlbumArtworkTheme(albumArt = coverArtFilePath) {
        Button(
            modifier = modifier,
            shape = CircleShape,
            contentPadding = PaddingValues(8.dp),
            onClick = onExpand,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (coverArtStatus) {
                        is CoverArtLoadingStatus.Loaded -> {
                            AsyncImage(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                model = coverArtStatus.filePath,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                        CoverArtLoadingStatus.Loading -> CoverArtLoading()
                        is CoverArtLoadingStatus.Error,
                        null,
                        -> AlbumCoverArtPlaceholder()
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            track,
                            maxLines = 1,
                        )
                        Text(
                            text = artist,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                        )
                    }
                }
            }

            IconButton(
                onClick = onPlayPause,
                colors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                )
            }

            IconButton(
                onClick = onNext,
                colors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun AlbumCoverArtPlaceholder() {
    Icon(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape),
        imageVector = Icons.Filled.PlayArrow,
        contentDescription = null,
        tint = Color.White.copy(alpha = 0.40f),
    )
}

@Composable
@Preview
private fun NowPlayingSnackPreview() {
    CassetteTheme {
        NowPlayingSnack(
            track = "Electric Feel",
            artist = "MGMT",
            coverArtFilePath = null,
            isPlaying = true,
            onPlayPause = {},
            onNext = {},
            onExpand = {},
        )
    }
}
