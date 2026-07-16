package fr.cassette.cassette.presentation.albumDetail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.domain.models.AlbumDetail
import fr.cassette.cassette.domain.models.Track
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassette.cassette.presentation.albumDetail.core.AlbumDetailContent
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.core.HomeMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    onEvent: (AlbumDetailEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var albumArtBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uiState.coverArt) {
        albumArtBitmap = uiState.coverArt?.let { coverArt ->
            BitmapFactory.decodeFile(coverArt.filePath)
        }
    }

    AlbumArtworkTheme(albumArt = albumArtBitmap) {
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
                        Column {
                            Text(
                                text = uiState.album?.name ?: ""
                            )

                            Text(
                                text = "${uiState.album?.artist ?: ""} - ${pluralStringResource(
                                    R.plurals.album_detail_tracks_count,
                                    uiState.album?.tracks?.size ?: 0,
                                    uiState.album?.tracks?.size ?: 0,
                                )}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onEvent(AlbumDetailEvent.OnBackClicked) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.album_detail_back),
                            )
                        }
                    }
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                when {
                    uiState.isLoading -> item { CircularProgressIndicator() }

                    uiState.hasError -> item {
                        HomeMessage(
                            title = stringResource(R.string.album_detail_error_title),
                            description = stringResource(R.string.album_detail_error_description),
                            actionLabel = stringResource(R.string.album_detail_retry),
                            onActionClick = { onEvent(AlbumDetailEvent.OnRetryClicked) },
                        )
                    }

                    uiState.album != null -> item {
                        AlbumDetailContent(
                            album = uiState.album,
                            coverArt = uiState.coverArt,
                            onTrackClick = { track -> onEvent(AlbumDetailEvent.OnTrackClicked(track.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun AlbumDetailScreenPreview() {
    CassetteTheme {
        AlbumDetailScreen(
            uiState = AlbumDetailUiState(
                albumId = "2YuwDgPuXhF5ir4SjAl6Iw",
                album = AlbumDetail(
                    id = "2YuwDgPuXhF5ir4SjAl6Iw",
                    name = "Discovery",
                    artist = "Daft Punk",
                    coverArt = "al-123",
                    created = "2026-07-15T12:00:00",
                    tracks = listOf(
                        Track(
                            id = "track-1",
                            title = "One More Time",
                            artist = "Daft Punk",
                            trackNumber = 1,
                            durationSeconds = 320,
                        ),
                        Track(
                            id = "track-2",
                            title = "Aerodynamic",
                            artist = "Daft Punk",
                            trackNumber = 2,
                            durationSeconds = 212,
                        ),
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}
