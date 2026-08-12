package fr.cassettelabs.cassette.presentation.playlistDetail.core

import cassette.shared.presentation.generated.resources.playlist_detail_delete
import cassette.shared.presentation.generated.resources.playlist_detail_tracks_count
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import fr.cassettelabs.cassette.presentation.core.BottomSheetItem
import fr.cassettelabs.cassette.presentation.core.CoverArtLoading
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlaylistDetailBottomSheet(
    playlistName: String,
    tracksCount: Int,
    coverArtStatus: CoverArtLoadingStatus?,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
                        text = playlistName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = pluralStringResource(Res.plurals.playlist_detail_tracks_count, tracksCount, tracksCount),
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
                title = stringResource(Res.string.playlist_detail_delete),
                icon = Icons.Rounded.Delete,
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    onDismissRequest()
                    onDeleteClick()
                },
            )
        }
    }
}
