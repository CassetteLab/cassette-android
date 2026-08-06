package fr.cassettelabs.cassette.presentation.nowPlaying

import cassette.shared.presentation.generated.resources.now_playing_title
import cassette.shared.presentation.generated.resources.now_playing_back
import cassette.shared.presentation.generated.resources.now_playing_queue
import cassette.shared.presentation.generated.resources.now_playing_unknown_title
import cassette.shared.presentation.generated.resources.now_playing_unknown_artist
import cassette.shared.presentation.generated.resources.now_playing_unknown_album
import cassette.shared.presentation.generated.resources.now_playing_starred
import cassette.shared.presentation.generated.resources.now_playing_shuffle
import cassette.shared.presentation.generated.resources.now_playing_previous
import cassette.shared.presentation.generated.resources.now_playing_pause
import cassette.shared.presentation.generated.resources.now_playing_play
import cassette.shared.presentation.generated.resources.now_playing_next
import cassette.shared.presentation.generated.resources.now_playing_repeat
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.cassettelabs.cassette.domain.models.CoverArtLoadingStatus
import fr.cassettelabs.cassette.domain.models.RepeatMode
import fr.cassettelabs.cassette.presentation.albumDetail.core.AlbumArtworkTheme
import fr.cassettelabs.cassette.presentation.core.AlbumCoverArt
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.nowPlaying.core.PlayerControlButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun NowPlayingScreen(
    uiState: NowPlayingUiState,
    onEvent: (NowPlayingEvent) -> Unit,
) {
    AlbumArtworkTheme(albumArt = (uiState.coverArtStatus as? CoverArtLoadingStatus.Loaded)?.filePath) {
        val playerContainer = MaterialTheme.colorScheme.primaryContainer
        val playerContent = MaterialTheme.colorScheme.onPrimaryContainer
        val playerAccent = MaterialTheme.colorScheme.primary
        val controlContainer = MaterialTheme.colorScheme.secondaryContainer
        val playPauseContainer = MaterialTheme.colorScheme.tertiaryContainer

        Scaffold(
            containerColor = playerContainer,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Text(
                            modifier = Modifier.padding(start = 18.dp),
                            text = stringResource(Res.string.now_playing_title),
                            color = playerContent,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            modifier =
                                Modifier
                                    .padding(start = 8.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.70f)),
                            onClick = { onEvent(NowPlayingEvent.OnBackClicked) },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ExpandMore,
                                contentDescription = stringResource(Res.string.now_playing_back),
                                tint = playerAccent,
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            modifier =
                                Modifier
                                    .padding(end = 14.dp)
                                    .size(width = 50.dp, height = 42.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 6.dp,
                                            topEnd = 50.dp,
                                            bottomStart = 6.dp,
                                            bottomEnd = 50.dp,
                                        ),
                                    ).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.70f)),
                            onClick = { onEvent(NowPlayingEvent.OnQueueClicked) },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = stringResource(Res.string.now_playing_queue),
                                tint = playerAccent,
                            )
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        AlbumCoverArt(
                            coverArtStatus = uiState.coverArtStatus,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(340.dp),
                            onImageLoaded = {},
                        )
                        if (uiState.coverArtStatus !is CoverArtLoadingStatus.Loaded && uiState.coverArtStatus != CoverArtLoadingStatus.Loading) {
                            Icon(
                                modifier = Modifier.size(110.dp),
                                imageVector = Icons.Rounded.Album,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.42f),
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                modifier = Modifier.basicMarquee(),
                                text = uiState.title.ifBlank { stringResource(Res.string.now_playing_unknown_title) },
                                color = playerContent,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                modifier = uiState.artistId?.let { artistId ->
                                    Modifier.clickable { onEvent(NowPlayingEvent.OnArtistClicked(artistId)) }
                                } ?: Modifier,
                                text =
                                    uiState.artist?.takeIf { it.isNotBlank() }
                                        ?: stringResource(Res.string.now_playing_unknown_artist),
                                color = playerContent.copy(alpha = 0.72f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text =
                                    uiState.album?.takeIf { it.isNotBlank() }
                                        ?: stringResource(Res.string.now_playing_unknown_album),
                                color = playerContent.copy(alpha = 0.54f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        IconButton(
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.70f)),
                            onClick = { onEvent(NowPlayingEvent.OnStarredClicked) },
                        ) {
                            Icon(
                                imageVector = if (uiState.isStarred) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = stringResource(Res.string.now_playing_starred),
                                tint = if (uiState.isStarred) MaterialTheme.colorScheme.tertiary else playerContent,
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Slider(
                            value = uiState.progress,
                            onValueChange = { onEvent(NowPlayingEvent.OnSeekChanged(it)) },
                            colors =
                                SliderDefaults.colors(
                                    thumbColor = playerContent,
                                    activeTrackColor = playerContent,
                                    inactiveTrackColor = playerContent.copy(alpha = 0.22f),
                                ),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = uiState.currentPositionLabel,
                                color = playerContent.copy(alpha = 0.72f),
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text = uiState.durationLabel,
                                color = playerContent.copy(alpha = 0.72f),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { onEvent(NowPlayingEvent.OnShuffleClicked) }) {
                            Icon(
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = stringResource(Res.string.now_playing_shuffle),
                                tint = if (uiState.isShuffleEnabled) playerAccent else playerContent.copy(alpha = 0.64f),
                            )
                        }
                        PlayerControlButton(
                            containerColor = controlContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = { onEvent(NowPlayingEvent.OnPreviousClicked) },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipPrevious,
                                contentDescription = stringResource(Res.string.now_playing_previous),
                                modifier = Modifier.size(34.dp),
                            )
                        }
                        PlayerControlButton(
                            modifier = Modifier.size(82.dp),
                            containerColor = playPauseContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = { onEvent(NowPlayingEvent.OnPlayPauseClicked) },
                        ) {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription =
                                    if (uiState.isPlaying) {
                                        stringResource(Res.string.now_playing_pause)
                                    } else {
                                        stringResource(Res.string.now_playing_play)
                                    },
                                modifier = Modifier.size(42.dp),
                            )
                        }
                        PlayerControlButton(
                            containerColor = controlContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = { onEvent(NowPlayingEvent.OnNextClicked) },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = stringResource(Res.string.now_playing_next),
                                modifier = Modifier.size(34.dp),
                            )
                        }
                        IconButton(onClick = { onEvent(NowPlayingEvent.OnRepeatClicked) }) {
                            Icon(
                                imageVector = if (uiState.repeatMode == RepeatMode.One) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                                contentDescription = stringResource(Res.string.now_playing_repeat),
                                tint = if (uiState.repeatMode == RepeatMode.Off) playerContent.copy(alpha = 0.64f) else playerAccent,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun NowPlayingScreenPreview() {
    CassetteTheme {
        NowPlayingScreen(
            uiState = NowPlayingUiState(),
            onEvent = {},
        )
    }
}
