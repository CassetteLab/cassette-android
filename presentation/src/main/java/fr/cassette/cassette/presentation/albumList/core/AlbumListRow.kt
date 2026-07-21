package fr.cassette.cassette.presentation.albumList.core

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.AlbumArtColors
import fr.cassette.cassette.presentation.core.extractAlbumArtColors

@Composable
internal fun AlbumListRow(
    album: AlbumList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var albumColors by remember { mutableStateOf<AlbumArtColors?>(null) }
    val defaultBackground = MaterialTheme.colorScheme.primaryContainer
    val defaultTextColor = MaterialTheme.colorScheme.onPrimaryContainer

    val animatedBackgroundColor by animateColorAsState(
        targetValue = albumColors?.backgroundColor ?: defaultBackground,
        animationSpec = tween(durationMillis = 400),
        label = "albumRowBgColor",
    )
    val animatedTextColor by animateColorAsState(
        targetValue = albumColors?.textColor ?: defaultTextColor,
        animationSpec = tween(durationMillis = 400),
        label = "albumRowTextColor",
    )
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(88.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick,
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight(),
            ) {
                if (album.coverArtFilePath != null) {
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(album.coverArtFilePath)
                                .allowHardware(false)
                                .crossfade(true)
                                .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onSuccess = { state ->
                            val bitmap = state.result.drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
                            albumColors = extractAlbumArtColors(bitmap)
                        },
                    )

                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors =
                                            listOf(
                                                Color.Transparent,
                                                animatedBackgroundColor,
                                            ),
                                    ),
                                ),
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(defaultBackground),
                    )
                }
            }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(animatedBackgroundColor)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = album.name,
                        color = animatedTextColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = album.artist ?: stringResource(R.string.album_list_unknown_artist),
                        color = animatedTextColor.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    album.created?.let { created ->
                        Text(
                            text = created,
                            color = animatedTextColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
