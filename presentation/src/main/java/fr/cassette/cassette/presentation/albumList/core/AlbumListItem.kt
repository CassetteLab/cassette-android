package fr.cassette.cassette.presentation.albumList.core

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
import fr.cassette.cassette.domain.models.AlbumList
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
internal fun AlbumListItem(
    album: AlbumList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var albumColors by remember(album.seedColor) {
        mutableStateOf(album.seedColor?.let(::seedColorToAlbumArtColors))
    }
    val defaultBackground = MaterialTheme.colorScheme.primaryContainer
    val defaultTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    val colorExtractionScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    DisposableEffect(Unit) {
        onDispose { colorExtractionScope.cancel() }
    }

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

    Button(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        if (albumColors == null) {
                            val bitmap = state.result.drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
                            colorExtractionScope.launch {
                                albumColors = extractAlbumArtColors(bitmap)
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

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(96.dp)
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Transparent,
                                        animatedBackgroundColor,
                                    ),
                            ),
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.BottomStart,
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
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun AlbumListItemPreview() {
    CassetteTheme {
        Column {
            AlbumListItem(
                album =
                    AlbumList(
                        id = "1",
                        name = "Little Dark Age",
                        artist = "MGMT",
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
