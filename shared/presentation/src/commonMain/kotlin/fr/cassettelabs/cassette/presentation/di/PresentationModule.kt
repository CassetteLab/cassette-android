package fr.cassettelabs.cassette.presentation.di

import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule =
    module {
        viewModelOf(::ServerConfigurationViewModel)
        viewModelOf(::OnBoardingCacheViewModel)
        viewModelOf(::OnBoardingCompleteViewModel)
        viewModelOf(::OnBoardingWelcomeViewModel)
    }
