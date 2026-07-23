package fr.cassette.cassette.domain.di

import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumTracksUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumUseCase
import fr.cassette.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassette.cassette.domain.usecases.GetAllPlaylistsUseCase
import fr.cassette.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassette.cassette.domain.usecases.GetPlaybackStateUseCase
import fr.cassette.cassette.domain.usecases.GetPlaylistCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassette.cassette.domain.usecases.GetServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.PausePlaybackUseCase
import fr.cassette.cassette.domain.usecases.PingServerUseCase
import fr.cassette.cassette.domain.usecases.PlayCurrentTrackUseCase
import fr.cassette.cassette.domain.usecases.PlayTrackUseCase
import fr.cassette.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassette.cassette.domain.usecases.RefreshPlaylistsUseCase
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.SeekPlaybackUseCase
import fr.cassette.cassette.domain.usecases.SetPlaybackRepeatModeUseCase
import fr.cassette.cassette.domain.usecases.SetPlaybackShuffleEnabledUseCase
import fr.cassette.cassette.domain.usecases.SkipToNextTrackUseCase
import fr.cassette.cassette.domain.usecases.SkipToPreviousTrackUseCase
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
        singleOf(::GetPlaylistCoverArtUseCase)
        singleOf(::RefreshPlaylistsUseCase)

        // Playback
        singleOf(::GetCurrentTrackUseCase)
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
