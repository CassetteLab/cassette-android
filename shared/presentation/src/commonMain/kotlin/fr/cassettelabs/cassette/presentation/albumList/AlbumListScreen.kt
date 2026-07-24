package fr.cassettelabs.cassette.presentation.albumList

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.album_list_empty_description
import cassette.shared.presentation.generated.resources.album_list_empty_title
import cassette.shared.presentation.generated.resources.album_list_refresh
import cassette.shared.presentation.generated.resources.album_list_title
import fr.cassettelabs.cassette.presentation.albumList.core.AlbumListItem
import fr.cassettelabs.cassette.presentation.home.HomeEvent
import fr.cassettelabs.cassette.presentation.home.HomeUiState
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AlbumListScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(HomeEvent.OnAppearing)
    }

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
                title = { Text(text = stringResource(Res.string.album_list_title)) },
                actions = {
                    Button(
                        enabled = !uiState.isRefreshing,
                        onClick = { onEvent(HomeEvent.OnRefresh) },
                    ) {
                        Text(text = stringResource(Res.string.album_list_refresh))
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.albums.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    HomeMessage(
                        title = stringResource(Res.string.album_list_empty_title),
                        description = stringResource(Res.string.album_list_empty_description),
                        actionLabel = stringResource(Res.string.album_list_refresh),
                        onActionClick = { onEvent(HomeEvent.OnRefresh) },
                    )
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
                    if (uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
                        )
                    }
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        contentPadding =
                            contentPadding.plus(
                                PaddingValues(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                                ),
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
                                onClick = { onEvent(HomeEvent.OnAlbumClicked(album.id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}
