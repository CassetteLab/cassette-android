package fr.cassette.cassette.domain.di

import fr.cassette.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumUseCase
import fr.cassette.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.PingServerUseCase
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    // Server Configuration
    singleOf(::PingServerUseCase)
    singleOf(::SaveServerConfigurationUseCase)
    singleOf(::HasServerConfigurationUseCase)

    // Albums
    singleOf(::GetAlbumUseCase)
    singleOf(::GetRecentlyAddedAlbumsUseCase)
}
