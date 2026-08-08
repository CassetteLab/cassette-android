package fr.cassettelabs.cassette.presentation.di

import fr.cassettelabs.cassette.presentation.albumDetail.AlbumDetailViewModel
import fr.cassettelabs.cassette.presentation.albumList.AlbumListViewModel
import fr.cassettelabs.cassette.presentation.artistDetail.ArtistDetailViewModel
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassettelabs.cassette.presentation.home.HomeViewModel
import fr.cassettelabs.cassette.presentation.main.MainViewModel
import fr.cassettelabs.cassette.presentation.nowPlaying.NowPlayingViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import fr.cassettelabs.cassette.presentation.playlistCreate.PlaylistCreateViewModel
import fr.cassettelabs.cassette.presentation.playlistDetail.PlaylistDetailViewModel
import fr.cassettelabs.cassette.presentation.playlistList.PlaylistListViewModel
import fr.cassettelabs.cassette.presentation.playbackQueue.PlaybackQueueViewModel
import fr.cassettelabs.cassette.presentation.settings.SettingsViewModel
import fr.cassettelabs.cassette.presentation.settings.logs.SettingsLogsViewModel
import fr.cassettelabs.cassette.presentation.starred.StarredViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule =
    module {
        viewModelOf(::ServerConfigurationViewModel)
        viewModelOf(::OnBoardingCacheViewModel)
        viewModelOf(::OnBoardingCompleteViewModel)
        viewModelOf(::OnBoardingWelcomeViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::MainViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::SettingsLogsViewModel)
        viewModelOf(::AlbumDetailViewModel)
        viewModelOf(::ArtistDetailViewModel)
        viewModelOf(::NowPlayingViewModel)
        viewModelOf(::AlbumListViewModel)
        viewModelOf(::PlaylistCreateViewModel)
        viewModelOf(::PlaylistListViewModel)
        viewModelOf(::PlaylistDetailViewModel)
        viewModelOf(::PlaybackQueueViewModel)
        viewModelOf(::StarredViewModel)
    }
