package fr.cassette.cassette.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.Album
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.AlbumRow
import fr.cassette.cassette.presentation.home.core.HomeMessage

@Composable
internal fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.home_recent_albums_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            when {
                uiState.isLoading -> item {
                    CircularProgressIndicator()
                }

                uiState.hasError -> item {
                    HomeMessage(
                        title = stringResource(R.string.home_recent_albums_error_title),
                        description = stringResource(R.string.home_recent_albums_error_description),
                        actionLabel = stringResource(R.string.home_recent_albums_retry),
                        onActionClick = { onEvent(HomeEvent.OnRetryClicked) },
                    )
                }

                uiState.albums.isEmpty() -> item {
                    HomeMessage(
                        title = stringResource(R.string.home_recent_albums_empty_title),
                        description = stringResource(R.string.home_recent_albums_empty_description),
                    )
                }

                else -> items(
                    items = uiState.albums,
                    key = { album -> album.id },
                ) { album ->
                    AlbumRow(album = album)
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun HomeScreenPreview() {
    CassetteTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                albums = listOf(
                    Album(
                        id = "1",
                        name = "Discovery",
                        artist = "Daft Punk",
                        coverArt = null,
                        created = null,
                    ),
                    Album(
                        id = "2",
                        name = "In Rainbows",
                        artist = "Radiohead",
                        coverArt = null,
                        created = null,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}
