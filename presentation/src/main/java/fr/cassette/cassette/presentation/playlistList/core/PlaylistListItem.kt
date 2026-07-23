package fr.cassette.cassette.presentation.playlistList.core

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.cassette.cassette.domain.models.PlaylistList
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.extractAlbumArtColors
import fr.cassette.cassette.presentation.core.seedColorToAlbumArtColors
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Composable
internal fun PlaylistListItem(
    playlist: PlaylistList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var playlistColors by remember(playlist.seedColor) {
        mutableStateOf(playlist.seedColor?.let(::seedColorToAlbumArtColors))
    }
    val defaultBackground = MaterialTheme.colorScheme.secondaryContainer
    val defaultTextColor = MaterialTheme.colorScheme.onSecondaryContainer
    val colorExtractionScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    DisposableEffect(Unit) {
        onDispose { colorExtractionScope.cancel() }
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = playlistColors?.backgroundColor ?: defaultBackground,
        animationSpec = tween(durationMillis = 400),
        label = "playlistItemBgColor",
    )
    val animatedTextColor by animateColorAsState(
        targetValue = playlistColors?.textColor ?: defaultTextColor,
        animationSpec = tween(durationMillis = 400),
        label = "playlistItemTextColor",
    )
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Button(
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
            ) {
                if (playlist.coverArtFilePath != null) {
                    AsyncImage(
                        model =
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(playlist.coverArtFilePath)
                                .allowHardware(false)
                                .crossfade(true)
                                .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onSuccess = { state ->
                            if (playlistColors == null) {
                                val bitmap = state.result.drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
                                colorExtractionScope.launch {
                                    playlistColors = extractAlbumArtColors(bitmap)
                                }
                            }
                        },
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
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(animatedBackgroundColor)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = playlist.name,
                        color = animatedTextColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.playlist_list_track_count, playlist.trackCount),
                        color = animatedTextColor.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PlaylistListItemPreview() {
    CassetteTheme {
        Column {
            PlaylistListItem(
                playlist =
                    PlaylistList(
                        id = "1",
                        name = "Chill Vibes",
                        trackCount = 18,
                        coverArt = "",
                        coverArtFilePath = "",
                        created = "",
                        seedColor = null,
                    ),
                onClick = { },
            )
        }
    }
}
