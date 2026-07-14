package fr.cassette.cassette.presentation.core.di

import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingComplete.OnBoardingCompleteViewModel
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.OnBoardingWelcomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::ServerConfigurationViewModel)
    viewModelOf(::OnBoardingCacheViewModel)
    viewModelOf(::OnBoardingCompleteViewModel)
    viewModelOf(::OnBoardingWelcomeViewModel)
}
