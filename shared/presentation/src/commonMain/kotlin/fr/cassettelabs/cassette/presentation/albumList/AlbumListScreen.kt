package fr.cassettelabs.cassette.presentation.albumList

import cassette.shared.presentation.generated.resources.album_list_title
import cassette.shared.presentation.generated.resources.album_list_loading_albums
import cassette.shared.presentation.generated.resources.album_list_empty_title
import cassette.shared.presentation.generated.resources.album_list_empty_description
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import fr.cassettelabs.cassette.domain.models.Album
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.albumList.core.AlbumListItem
import fr.cassettelabs.cassette.presentation.core.LoadingMessage
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AlbumListScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: AlbumListUiState,
    onEvent: (AlbumListEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(AlbumListEvent.OnAppearing)
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.album_list_title),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
            onRefresh = { onEvent(AlbumListEvent.OnRefresh) },
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isPullToRefreshIndicatorVisible && uiState.isRefreshing,
                    state = pullToRefreshState,
                )
            },
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingMessage(
                        message = Res.string.album_list_loading_albums,
                    )
                }
            } else if (uiState.albums.isEmpty()) {
                HomeMessage(
                    title = stringResource(Res.string.album_list_empty_title),
                    description = stringResource(Res.string.album_list_empty_description),
                )
            } else {
                LazyVerticalGrid(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding =
                        contentPadding.plus(
                            PaddingValues(
                                top = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                            )
                        ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        AnimatedVisibility(uiState.isRefreshing) {
                            LinearWavyProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    items(
                        items = uiState.albums,
                        key = { album -> album.id },
                    ) { album ->
                        val coverArtStatus = uiState.albumCoverArtStatuses[album.id]
                        val coverArtFilePath = (coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath ?: album.coverArtFilePath
                        AlbumArtworkTheme(albumArt = coverArtFilePath) {
                            AlbumListItem(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                album = album,
                                coverArtStatus = coverArtStatus,
                                onClick = { onEvent(AlbumListEvent.OnAlbumClicked(album.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun AlbumListScreenPreview() {
    CassetteTheme {
        AlbumListScreen(
            uiState =
                AlbumListUiState(
                    isLoading = false,
                    albums =
                        listOf(
                            Album(
                                id = "1",
                                name = "Discovery",
                                artist = "Daft Punk",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                seedColor = null,
                            ),
                            Album(
                                id = "2",
                                name = "In Rainbows",
                                artist = "Radiohead",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                seedColor = null,
                            ),
                        ),
                ),
            onEvent = {},
        )
    }
}

@Composable
@PreviewLightDark
private fun AlbumListScreenLoadingPreview() {
    CassetteTheme {
        AlbumListScreen(
            uiState =
                AlbumListUiState(
                    isLoading = true,
                    albums = emptyList()
                ),
            onEvent = {},
        )
    }
}
