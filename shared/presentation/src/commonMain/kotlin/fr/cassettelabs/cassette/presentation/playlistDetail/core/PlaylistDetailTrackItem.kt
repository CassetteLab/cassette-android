package fr.cassettelabs.cassette.presentation.playlistDetail.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.CoverArtLoading
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.core.track.TrackButton
import fr.cassettelabs.cassette.presentation.core.track.TrackMoreButton

@Composable
internal fun PlaylistDetailTrackItem(
    modifier: Modifier = Modifier,
    track: Track,
    coverArtStatus: CoverArtLoadingStatus? = track.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                val loadedCoverArtPath = (coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: track.coverArtFilePath
                if (loadedCoverArtPath != null) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        model = loadedCoverArtPath,
                        contentDescription = null,
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
        },
        trailing = {
            TrackMoreButton(
                onClick = onMoreClick
            )
        },
    )
}

@Preview
@Composable
private fun PlaylistDetailTrackItemPreview() {
    CassetteTheme {
        PlaylistDetailTrackItem(
            track =
                Track(
                    id = "",
                    title = "Hand It Over",
                    artist = "MGMT",
                    trackNumber = 1,
                    durationSeconds = 146,
                ),
            onClick = {},
        )
    }
}
