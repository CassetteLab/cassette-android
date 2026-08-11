package fr.cassettelabs.cassette.presentation.albumDetail.core

import cassette.shared.presentation.generated.resources.album_detail_track_add_to_playlist
import cassette.shared.presentation.generated.resources.album_detail_track_add_to_queue
import cassette.shared.presentation.generated.resources.album_detail_track_dislike
import cassette.shared.presentation.generated.resources.album_detail_track_like
import cassette.shared.presentation.generated.resources.album_detail_unknown_artist
import cassette.shared.presentation.generated.resources.Res

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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Track
import fr.cassettelabs.cassette.presentation.core.BottomSheetItem
import fr.cassettelabs.cassette.presentation.core.CoverArtLoading
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailTrackBottomSheet(
    track: Track,
    coverArtStatus: CoverArtLoadingStatus?,
    isLiked: Boolean,
    onDismiss: () -> Unit,
    onLikeClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onAddToQueueClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (coverArtStatus) {
                    is CoverArtLoadingStatus.Loaded -> {
                        AsyncImage(
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                            model = coverArtStatus.filePath,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                    CoverArtLoadingStatus.Loading -> {
                        CoverArtLoading(modifier = Modifier.size(56.dp))
                    }
                    is CoverArtLoadingStatus.Error,
                    null,
                    -> {
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = track.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = track.artist ?: stringResource(Res.string.album_detail_unknown_artist),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            BottomSheetItem(
                title = stringResource(
                    if (isLiked) Res.string.album_detail_track_dislike
                    else Res.string.album_detail_track_like
                ),
                icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                color = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onDismiss()
                    onLikeClick()
                }
            )

            BottomSheetItem(
                title = stringResource(Res.string.album_detail_track_add_to_playlist),
                icon = Icons.Rounded.PlaylistAdd,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onDismiss()
                    onAddToPlaylistClick()
                }
            )

            BottomSheetItem(
                title = stringResource(Res.string.album_detail_track_add_to_queue),
                icon = Icons.Rounded.QueueMusic,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = {
                    onDismiss()
                    onAddToQueueClick()
                }
            )
        }
    }
}
