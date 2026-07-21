package fr.cassette.cassette.presentation.core

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.cassette.cassette.domain.models.AlbumCoverArt as AlbumCoverArtModel

@Composable
internal fun AlbumCoverArt(
    coverArt: AlbumCoverArtModel?,
    modifier: Modifier = Modifier,
    onBitmapLoaded: (Bitmap) -> Unit = {},
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
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(coverArt.filePath)
                        .allowHardware(false)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onSuccess = { state ->
                    onBitmapLoaded(state.result.drawable.toBitmap(config = Bitmap.Config.ARGB_8888))
                },
            )
        }
    }
}
