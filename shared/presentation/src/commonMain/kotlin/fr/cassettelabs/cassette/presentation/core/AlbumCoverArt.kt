package fr.cassettelabs.cassette.presentation.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus

@Composable
internal fun AlbumCoverArt(
    coverArtStatus: CoverArtLoadingStatus?,
    modifier: Modifier = Modifier,
    onImageLoaded: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        when (coverArtStatus) {
            is CoverArtLoadingStatus.Loaded -> {
                AsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = coverArtStatus.filePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    onSuccess = { onImageLoaded() },
                )
            }
            CoverArtLoadingStatus.Loading -> CoverArtLoading()
            is CoverArtLoadingStatus.Error,
            null,
            -> Unit
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CoverArtLoading(modifier: Modifier = Modifier) {
    CircularWavyProgressIndicator(
        modifier = modifier.size(28.dp),
        color = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}
