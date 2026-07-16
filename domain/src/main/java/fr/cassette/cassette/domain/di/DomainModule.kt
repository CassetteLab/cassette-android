package fr.cassette.cassette.domain.di

import fr.cassette.cassette.domain.usecases.GetAlbumCoverArtUseCase
import fr.cassette.cassette.domain.usecases.GetCurrentTrackUseCase
import fr.cassette.cassette.domain.usecases.GetRecentlyAddedAlbumsUseCase
import fr.cassette.cassette.domain.usecases.GetAlbumUseCase
import fr.cassette.cassette.domain.usecases.GetServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.HasServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.PingServerUseCase
import fr.cassette.cassette.domain.usecases.SaveServerConfigurationUseCase
import fr.cassette.cassette.domain.usecases.SetCurrentTrackUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    // Server Configuration
    singleOf(::PingServerUseCase)
    singleOf(::GetServerConfigurationUseCase)
    singleOf(::SaveServerConfigurationUseCase)
    singleOf(::HasServerConfigurationUseCase)

    // Albums
    singleOf(::GetAlbumUseCase)
    singleOf(::GetAlbumCoverArtUseCase)
    singleOf(::GetRecentlyAddedAlbumsUseCase)

    // Playback
    singleOf(::GetCurrentTrackUseCase)
    singleOf(::SetCurrentTrackUseCase)
}
