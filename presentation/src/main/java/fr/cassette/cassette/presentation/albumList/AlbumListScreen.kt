package fr.cassette.cassette.presentation.albumList

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumList.core.AlbumListItem
import fr.cassette.cassette.presentation.core.LoadingMessage
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.HomeMessage

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
                    Column {
                        Text(text = stringResource(R.string.album_list_title))
                        AnimatedVisibility(uiState.isRefreshing) {
                            LinearWavyProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
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
                        message = R.string.album_list_loading_albums,
                    )
                }
            } else if (uiState.albums.isEmpty()) {
                HomeMessage(
                    title = stringResource(R.string.album_list_empty_title),
                    description = stringResource(R.string.album_list_empty_description),
                )
            } else {
                LazyVerticalGrid(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding =
                        contentPadding.plus(
                            PaddingValues(
                                start = 16.dp,
                                top = 16.dp,
                                end = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                            )
                        ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = uiState.albums,
                        key = { album -> album.id },
                    ) { album ->
                        AlbumListItem(
                            album = album,
                            onClick = { onEvent(AlbumListEvent.OnAlbumClicked(album.id)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
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
                            AlbumList(
                                id = "1",
                                name = "Discovery",
                                artist = "Daft Punk",
                                coverArt = null,
                                coverArtFilePath = null,
                                created = null,
                                seedColor = null,
                            ),
                            AlbumList(
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
