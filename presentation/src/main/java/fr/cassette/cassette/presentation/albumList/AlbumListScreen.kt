package fr.cassette.cassette.presentation.albumList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumList
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumList.core.AlbumListRow
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.HomeMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumListScreen(
    uiState: AlbumListUiState,
    onEvent: (AlbumListEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        onEvent(AlbumListEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(text = stringResource(R.string.album_list_title))
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                uiState.isLoading -> item {
                    CircularProgressIndicator()
                }

                uiState.hasError -> item {
                    HomeMessage(
                        title = stringResource(R.string.album_list_error_title),
                        description = stringResource(R.string.album_list_error_description),
                        actionLabel = stringResource(R.string.album_list_retry),
                        onActionClick = { onEvent(AlbumListEvent.OnRetryClicked) },
                    )
                }

                uiState.albums.isEmpty() -> item {
                    HomeMessage(
                        title = stringResource(R.string.album_list_empty_title),
                        description = stringResource(R.string.album_list_empty_description),
                    )
                }

                else -> items(
                    items = uiState.albums,
                    key = { album -> album.id },
                ) { album ->
                    AlbumListRow(
                        album = album,
                        onClick = { onEvent(AlbumListEvent.OnAlbumClicked(album.id)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
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
            uiState = AlbumListUiState(
                isLoading = false,
                albums = listOf(
                    AlbumList(
                        id = "1",
                        name = "Discovery",
                        artist = "Daft Punk",
                        coverArt = null,
                        coverArtFilePath = null,
                        created = null,
                    ),
                    AlbumList(
                        id = "2",
                        name = "In Rainbows",
                        artist = "Radiohead",
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
