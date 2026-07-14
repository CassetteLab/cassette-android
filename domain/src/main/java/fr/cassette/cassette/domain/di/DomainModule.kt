package fr.cassette.cassette.domain.di

import fr.cassette.cassette.domain.usecases.PingServerUseCase
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::PingServerUseCase)
    singleOf(::SaveServerConfigurationUseCase)
}
