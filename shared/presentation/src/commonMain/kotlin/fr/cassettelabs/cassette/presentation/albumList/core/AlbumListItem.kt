package fr.cassettelabs.cassette.presentation.albumList.core

import cassette.shared.presentation.generated.resources.album_list_unknown_artist
import cassette.shared.presentation.generated.resources.Res

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.core.CoverArtLoading
import fr.cassettelabs.cassette.presentation.core.seedColorToAlbumArtColors
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun AlbumListItem(
    modifier: Modifier = Modifier,
    album: Album,
    coverArtStatus: CoverArtLoadingStatus? = album.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
    onClick: () -> Unit,
) {
    var albumColors by remember(album.seedColor) {
        mutableStateOf(album.seedColor?.let(::seedColorToAlbumArtColors))
    }
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
            val loadedCoverArtPath = (coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: album.coverArtFilePath
            if (loadedCoverArtPath != null) {
                AsyncImage(
                    model = loadedCoverArtPath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
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
                    contentAlignment = Alignment.Center,
                ) {
                    if (coverArtStatus == CoverArtLoadingStatus.Loading) {
                        CoverArtLoading()
                    }
                }
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
                        text = album.artist ?: stringResource(Res.string.album_list_unknown_artist),
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
@Preview
private fun AlbumListItemPreview() {
    CassetteTheme {
        Column {
            AlbumListItem(
                album =
                    Album(
                        id = "1",
                        name = "Little Dark Age",
                        artist = "MGMT",
                        coverArt = "",
                        coverArtFilePath = "",
                        created = "",
                        seedColor = null,
                    ),
                coverArtStatus = null,
                onClick = { },
            )
        }
    }
}
