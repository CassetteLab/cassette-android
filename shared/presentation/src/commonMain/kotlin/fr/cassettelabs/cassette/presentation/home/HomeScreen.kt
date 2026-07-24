package fr.cassettelabs.cassette.presentation.home

import cassette.shared.presentation.generated.resources.home_recent_albums_title
import cassette.shared.presentation.generated.resources.home_recent_albums_error_title
import cassette.shared.presentation.generated.resources.home_recent_albums_error_description
import cassette.shared.presentation.generated.resources.home_recent_albums_retry
import cassette.shared.presentation.generated.resources.home_recent_albums_empty_title
import cassette.shared.presentation.generated.resources.home_recent_albums_empty_description
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.domain.models.AlbumList
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.home.core.AlbumRow
import fr.cassettelabs.cassette.presentation.home.core.HomeMessage

@Composable
internal fun HomeScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentPadding =
                innerPadding
                    .plus(other = contentPadding)
                    .plus(other = PaddingValues(all = 16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.home_recent_albums_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            when {
                uiState.isLoading ->
                    item {
                        CircularProgressIndicator()
                    }

                uiState.hasError ->
                    item {
                        HomeMessage(
                            title = stringResource(Res.string.home_recent_albums_error_title),
                            description = stringResource(Res.string.home_recent_albums_error_description),
                            actionLabel = stringResource(Res.string.home_recent_albums_retry),
                            onActionClick = { onEvent(HomeEvent.OnRetryClicked) },
                        )
                    }

                uiState.albums.isEmpty() ->
                    item {
                        HomeMessage(
                            title = stringResource(Res.string.home_recent_albums_empty_title),
                            description = stringResource(Res.string.home_recent_albums_empty_description),
                        )
                    }

                else ->
                    items(
                        items = uiState.albums,
                        key = { album -> album.id },
                    ) { album ->
                        AlbumRow(
                            album = album,
                            coverArt = uiState.albumCoverArts[album.id],
                            onClick = { onEvent(HomeEvent.OnAlbumClicked(album.id)) },
                        )
                    }
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    CassetteTheme {
        HomeScreen(
            uiState =
                HomeUiState(
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
