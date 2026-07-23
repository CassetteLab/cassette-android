package fr.cassette.cassette.presentation.core.di

import fr.cassette.cassette.presentation.albumDetail.AlbumDetailViewModel
import fr.cassette.cassette.presentation.albumList.AlbumListViewModel
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.home.HomeViewModel
import fr.cassette.cassette.presentation.main.MainViewModel
import fr.cassette.cassette.presentation.nowPlaying.NowPlayingViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import fr.cassette.cassette.presentation.playlistDetail.PlaylistDetailViewModel
import fr.cassette.cassette.presentation.playlistList.PlaylistListViewModel
import fr.cassette.cassette.presentation.settings.SettingsViewModel
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
        viewModelOf(::AlbumDetailViewModel)
        viewModelOf(::NowPlayingViewModel)
        viewModelOf(::AlbumListViewModel)
        viewModelOf(::PlaylistListViewModel)
        viewModelOf(::PlaylistDetailViewModel)
    }
