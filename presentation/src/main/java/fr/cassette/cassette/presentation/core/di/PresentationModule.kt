package fr.cassette.cassette.presentation.core.di

import fr.cassette.cassette.presentation.onBoarding.onBoardingCache.OnBoardingCacheViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::OnBoardingCacheViewModel)
}