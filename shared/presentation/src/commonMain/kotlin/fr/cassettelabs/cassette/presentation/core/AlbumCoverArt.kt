package fr.cassettelabs.cassette.presentation.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel

@Composable
internal fun AlbumCoverArt(
    coverArt: AlbumCoverArtModel?,
    modifier: Modifier = Modifier,
    onImageLoaded: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (coverArt != null) {
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = coverArt.filePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onSuccess = { onImageLoaded() },
            )
        }
    }
}
