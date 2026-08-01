package fr.cassettelabs.cassette.presentation.artistDetail

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.artist_detail_albums
import cassette.shared.presentation.generated.resources.artist_detail_albums_loading_message
import cassette.shared.presentation.generated.resources.artist_detail_loading_message
import cassette.shared.presentation.generated.resources.artist_detail_no_albums
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.Artist
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumDetailBackButton
import fr.cassettelabs.cassette.presentation.artistDetail.core.ArtistDetailHeader
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.AlbumRow
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArtistDetailScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: ArtistDetailUiState,
    onEvent: (ArtistDetailEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(ArtistDetailEvent.OnAppearing)
    }

    val pullToRefreshState = rememberPullToRefreshState()

    AlbumArtworkTheme(albumArt = (uiState.coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: uiState.artist?.coverArtFilePath) {
        when {
            uiState.isLoading && uiState.artist == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingMessage(message = Res.string.artist_detail_loading_message)
                }
            }
            else -> {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                    isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                    onRefresh = { onEvent(ArtistDetailEvent.OnRefresh) },
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                            state = pullToRefreshState,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding.plus(PaddingValues(bottom = 16.dp)),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            ArtistDetailHeader(
                                artist = uiState.artist,
                                coverArtStatus = uiState.coverArtStatus,
                            )
                        }

                        item {
                            Text(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                text = stringResource(Res.string.artist_detail_albums),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        if (uiState.isAlbumsLoading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    LoadingMessage(message = Res.string.artist_detail_albums_loading_message)
                                }
                            }
                        } else if (uiState.albums.isEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp),
                                    text = stringResource(Res.string.artist_detail_no_albums),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        items(uiState.albums, key = { album -> album.id }) { album ->
                            AlbumRow(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                album = album,
                                coverArtStatus = album.coverArtFilePath?.let { CoverArtLoadingStatus.Loaded(it) },
                                onClick = { onEvent(ArtistDetailEvent.OnAlbumClicked(album.id)) },
                            )
                        }
                    }

                    AlbumDetailBackButton(onClick = { onEvent(ArtistDetailEvent.OnBackClicked) })
                }
            }
        }
    }
}

@Composable
@Preview
private fun ArtistDetailScreenPreview() {
    CassetteTheme {
        ArtistDetailScreen(
            uiState =
                ArtistDetailUiState(
                    artist = Artist(id = "artist-1", name = "Daft Punk", albumCount = 2),
                    albums =
                        listOf(
                            Album(
                                id = "album-1",
                                name = "Discovery",
                                artist = "Daft Punk",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = "2001",
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}
