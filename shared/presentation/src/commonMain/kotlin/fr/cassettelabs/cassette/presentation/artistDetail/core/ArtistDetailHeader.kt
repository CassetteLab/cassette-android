package fr.cassettelabs.cassette.presentation.artistDetail.core

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.artist_detail_album_count_format
import cassette.shared.presentation.generated.resources.artist_detail_unknown_artist
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.core.AlbumCoverArt
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ArtistDetailHeader(
    artist: Artist?,
    coverArtStatus: CoverArtLoadingStatus?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .size(190.dp)
                    .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AlbumCoverArt(
                coverArtStatus = coverArtStatus ?: artist?.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
                modifier = Modifier.matchParentSize(),
            )
            if (coverArtStatus !is CoverArtLoadingStatus.Loaded && artist?.coverArtFilePath == null) {
                Icon(
                    modifier = Modifier.size(88.dp),
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.55f),
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = artist?.name ?: stringResource(Res.string.artist_detail_unknown_artist),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )
        artist?.let {
            Text(
                text = stringResource(Res.string.artist_detail_album_count_format, it.albumCount),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview
private fun ArtistDetailHeaderPreview() {
    CassetteTheme {
        ArtistDetailHeader(
            artist = Artist(id = "artist-1", name = "Daft Punk", albumCount = 2),
            coverArtStatus = null,
        )
    }
}
