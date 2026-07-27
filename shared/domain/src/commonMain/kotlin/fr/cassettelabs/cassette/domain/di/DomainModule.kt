package fr.cassettelabs.cassette.domain.di

import fr.cassettelabs.cassette.domain.usecases.album.GetAlbumCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.album.GetAlbumTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.album.GetAlbumUseCase
import fr.cassettelabs.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.GetAllPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaybackStateUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaybackQueueUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistTracksUseCase
import fr.cassettelabs.cassette.domain.usecases.GetPlaylistUseCase
import fr.cassettelabs.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.GetServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.PausePlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.PingServerUseCase
import fr.cassettelabs.cassette.domain.usecases.PlayCurrentTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.PlayTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.RefreshPlaylistsUseCase
import fr.cassettelabs.cassette.domain.usecases.SaveServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.SeekPlaybackUseCase
import fr.cassettelabs.cassette.domain.usecases.SetPlaybackRepeatModeUseCase
import fr.cassettelabs.cassette.domain.usecases.SetPlaybackShuffleEnabledUseCase
import fr.cassettelabs.cassette.domain.usecases.SkipToNextTrackUseCase
import fr.cassettelabs.cassette.domain.usecases.SkipToPreviousTrackUseCase
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
        singleOf(::GetAlbumCoverArtUseCase)
        singleOf(::GetRecentlyAddedAlbumsUseCase)
        singleOf(::GetAllAlbumsUseCase)
        singleOf(::RefreshAlbumsUseCase)

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
        singleOf(::SeekPlaybackUseCase)
        singleOf(::SetPlaybackRepeatModeUseCase)
        singleOf(::SetPlaybackShuffleEnabledUseCase)
        singleOf(::SkipToNextTrackUseCase)
        singleOf(::SkipToPreviousTrackUseCase)
    }
