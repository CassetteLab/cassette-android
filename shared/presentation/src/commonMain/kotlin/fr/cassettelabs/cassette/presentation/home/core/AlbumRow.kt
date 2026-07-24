package fr.cassettelabs.cassette.presentation.home.core

import cassette.shared.presentation.generated.resources.home_unknown_artist
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.presentation.core.AlbumCoverArt
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel

@Composable
internal fun AlbumRow(
    album: AlbumList,
    coverArt: AlbumCoverArtModel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panelColor = MaterialTheme.colorScheme.primaryContainer
    val panelContentColor = MaterialTheme.colorScheme.onPrimaryContainer

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onClick)
                .height(96.dp)
                .background(panelColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
        ) {
            AlbumCoverArt(
                coverArt = coverArt,
                modifier = Modifier.matchParentSize(),
            )
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, panelColor),
                            ),
                        ),
            )
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = album.name,
                color = panelContentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = album.artist ?: stringResource(Res.string.home_unknown_artist),
                color = panelContentColor.copy(alpha = 0.78f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
            )
            album.created?.let { created ->
                Text(
                    text = created,
                    color = panelContentColor.copy(alpha = 0.58f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
