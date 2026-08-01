package fr.cassettelabs.cassette.presentation.home

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.home_albums_title
import cassette.shared.presentation.generated.resources.home_artists_description
import cassette.shared.presentation.generated.resources.home_artists_title
import cassette.shared.presentation.generated.resources.home_downloads_description
import cassette.shared.presentation.generated.resources.home_downloads_title
import cassette.shared.presentation.generated.resources.home_playlists_title
import cassette.shared.presentation.generated.resources.home_queue_description
import cassette.shared.presentation.generated.resources.home_queue_title
import cassette.shared.presentation.generated.resources.home_show_all
import cassette.shared.presentation.generated.resources.home_starred_description
import cassette.shared.presentation.generated.resources.home_starred_title
import cassette.shared.presentation.generated.resources.home_subtitle
import cassette.shared.presentation.generated.resources.home_title
import cassette.shared.presentation.generated.resources.home_tracks_description
import cassette.shared.presentation.generated.resources.home_tracks_title
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.Playlist
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumList.core.AlbumListItem
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.HomePreviewSection
import fr.cassettelabs.cassette.presentation.home.core.HomeSectionCard
import fr.cassettelabs.cassette.presentation.playlistList.core.PlaylistListItem
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding =
                innerPadding
                    .plus(contentPadding)
                    .plus(PaddingValues(vertical = 24.dp)),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    text = stringResource(Res.string.home_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp, start = 12.dp),
                    text = stringResource(Res.string.home_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            item {
                HomeSectionCard(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    title = stringResource(Res.string.home_artists_title),
                    description = stringResource(Res.string.home_artists_description),
                    icon = Icons.Default.Person,
                    onClick = { onEvent(HomeEvent.OnArtistsClicked) },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HomePreviewSection(
                    title = stringResource(Res.string.home_albums_title),
                    actionLabel = stringResource(Res.string.home_show_all),
                    onActionClick = { onEvent(HomeEvent.OnAlbumsClicked) },
                ) {
                    items(uiState.albums, key = { album -> album.id }) { album ->
                        LaunchedEffect(album.id) {
                            if (album.coverArtFilePath.isNullOrBlank()) {
                                onEvent(HomeEvent.OnAlbumCoverArtAppeared(album.id))
                            }
                        }

                        val coverArtStatus = uiState.albumCoverArtStatuses[album.id]
                        val coverArtFilePath = (coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: album.coverArtFilePath
                        AlbumArtworkTheme(albumArt = coverArtFilePath) {
                            AlbumListItem(
                                modifier = Modifier.width(160.dp),
                                album = album,
                                coverArtStatus = coverArtStatus,
                                onClick = { onEvent(HomeEvent.OnAlbumClicked(album.id)) },
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HomePreviewSection(
                    title = stringResource(Res.string.home_playlists_title),
                    actionLabel = stringResource(Res.string.home_show_all),
                    onActionClick = { onEvent(HomeEvent.OnPlaylistsClicked) },
                ) {
                    items(uiState.playlists, key = { playlist -> playlist.id }) { playlist ->
                        LaunchedEffect(playlist.id) {
                            if (playlist.coverArtFilePath.isNullOrBlank()) {
                                onEvent(HomeEvent.OnPlaylistCoverArtAppeared(playlist.id))
                            }
                        }

                        PlaylistListItem(
                            modifier = Modifier.width(160.dp),
                            playlist = playlist,
                            coverArtStatus = uiState.playlistCoverArtStatuses[playlist.id],
                            onClick = { onEvent(HomeEvent.OnPlaylistClicked(playlist.id)) },
                        )
                    }
                }
            }

            item {
                HomeSectionCard(
                    title = stringResource(Res.string.home_tracks_title),
                    description = stringResource(Res.string.home_tracks_description),
                    icon = Icons.Default.MusicNote,
                    onClick = { onEvent(HomeEvent.OnTracksClicked) },
                )
            }

            item {
                HomeSectionCard(
                    title = stringResource(Res.string.home_starred_title),
                    description = stringResource(Res.string.home_starred_description),
                    icon = Icons.Default.Star,
                    onClick = { onEvent(HomeEvent.OnStarredClicked) },
                )
            }

            item {
                HomeSectionCard(
                    title = stringResource(Res.string.home_downloads_title),
                    description = stringResource(Res.string.home_downloads_description),
                    icon = Icons.Default.Download,
                    onClick = { onEvent(HomeEvent.OnDownloadsClicked) },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeSectionCard(
                    title = stringResource(Res.string.home_queue_title),
                    description = stringResource(Res.string.home_queue_description),
                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                    onClick = { onEvent(HomeEvent.OnQueueClicked) },
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun HomeScreenPreview() {
    CassetteTheme {
        HomeScreen(
            uiState =
                HomeUiState(
                    albums =
                        listOf(
                            Album(
                                id = "1",
                                name = "Discovery",
                                artist = "Daft Punk",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                            ),
                            Album(
                                id = "2",
                                name = "In Rainbows",
                                artist = "Radiohead",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                            ),
                        ),
                    playlists =
                        listOf(
                            Playlist(
                                id = "1",
                                name = "Late Night",
                                trackCount = 42,
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                            ),
                            Playlist(
                                id = "2",
                                name = "Road Trip",
                                trackCount = 18,
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}
