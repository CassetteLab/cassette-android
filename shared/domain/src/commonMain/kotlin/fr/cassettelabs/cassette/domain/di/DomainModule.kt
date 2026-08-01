package fr.cassettelabs.cassette.domain.di

import fr.cassettelabs.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.GetAlbumUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.RefreshAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.albumDetail.RefreshAlbumUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.GetArtistAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.GetArtistUseCase
import fr.cassettelabs.cassette.domain.usecases.artistDetail.RefreshArtistUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.GetAllPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.GetPlaybackStateUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.GetPlaybackQueueUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistDetail.GetPlaylistTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.configuration.GetServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.configuration.HasServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PausePlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.configuration.PingServerUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PlayCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.PlayTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.ReorderPlaybackQueueUseCase
import fr.cassettelabs.cassette.domain.usecases.albumList.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.playlistList.RefreshPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.configuration.SaveServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.SeekPlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.SetPlaybackRepeatModeUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.SetPlaybackShuffleEnabledUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.SkipToNextTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.playback.SkipToPreviousTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.starred.GetStarredLibraryUseCase
import fr.cassettelabs.cassette.domain.usecases.starred.SetTrackStarredUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule =
    module {
        // Server Configuration
        singleOf(::PingServerUseCase)
        singleOf(::GetServerConfigurationUseCase)
        singleOf(::SaveServerConfigurationUseCase)
        singleOf(::HasServerConfigurationUseCase)

        // Albums
        singleOf(::GetAlbumUseCase)
        singleOf(::GetAlbumTracksUseCase)
        singleOf(::RefreshAlbumUseCase)
        singleOf(::RefreshAlbumTracksUseCase)
        singleOf(::GetAlbumCoverArtUseCase)
        singleOf(::GetRecentlyAddedAlbumsUseCase)
        singleOf(::GetAllAlbumsUseCase)
        singleOf(::RefreshAlbumsUseCase)
        singleOf(::GetStarredLibraryUseCase)
        singleOf(::SetTrackStarredUseCase)
        singleOf(::GetArtistUseCase)
        singleOf(::GetArtistAlbumsUseCase)
        singleOf(::RefreshArtistUseCase)

        // Playlists
        singleOf(::GetAllPlaylistsUseCase)
        singleOf(::GetPlaylistUseCase)
        singleOf(::GetPlaylistTracksUseCase)
        singleOf(::GetPlaylistCoverArtUseCase)
        singleOf(::RefreshPlaylistsUseCase)

        // Playback
        singleOf(::GetCurrentTrackUseCase)
        singleOf(::GetPlaybackQueueUseCase)
        singleOf(::GetPlaybackStateUseCase)
        singleOf(::PausePlaybackUseCase)
        singleOf(::PlayCurrentTrackUseCase)
        singleOf(::PlayTrackUseCase)
        singleOf(::ReorderPlaybackQueueUseCase)
        singleOf(::SeekPlaybackUseCase)
        singleOf(::SetPlaybackRepeatModeUseCase)
        singleOf(::SetPlaybackShuffleEnabledUseCase)
        singleOf(::SkipToNextTrackUseCase)
        singleOf(::SkipToPreviousTrackUseCase)
    }
