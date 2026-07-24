package fr.cassettelabs.cassette.domain.di

import fr.cassettelabs.cassette.domain.usecases.GetAllAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.GetServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassettelabs.cassette.domain.usecases.PingServerUseCase
import fr.cassettelabs.cassette.domain.usecases.RefreshAlbumsUseCase
import fr.cassettelabs.cassette.domain.usecases.SaveServerConfigurationUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule =
    module {
        singleOf(::PingServerUseCase)
        singleOf(::GetServerConfigurationUseCase)
        singleOf(::SaveServerConfigurationUseCase)
        singleOf(::HasServerConfigurationUseCase)
        singleOf(::GetAllAlbumsUseCase)
        singleOf(::RefreshAlbumsUseCase)
    }
