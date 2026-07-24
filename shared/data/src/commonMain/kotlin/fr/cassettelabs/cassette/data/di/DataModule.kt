package fr.cassettelabs.cassette.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fr.cassettelabs.cassette.data.local.CassetteDatabase
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.local.dao.AlbumDao
import fr.cassettelabs.cassette.data.local.dao.PlaybackQueueDao
import fr.cassettelabs.cassette.data.local.dao.PlaylistDao
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.local.dao.TrackDao
import fr.cassettelabs.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.datasources.PlaylistRemoteDataSourceImpl
import fr.cassettelabs.cassette.data.remote.ktor.KtorClientProviderImpl
import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestAuthenticationPluginProvider
import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestDefaultsPluginProvider
import fr.cassettelabs.cassette.data.repositories.AlbumRepositoryImpl
import fr.cassettelabs.cassette.data.repositories.PlaybackRepositoryImpl
import fr.cassettelabs.cassette.data.repositories.PlaylistRepositoryImpl
import fr.cassettelabs.cassette.data.repositories.ServerConfigurationRepositoryImpl
import fr.cassettelabs.cassette.domain.repositories.AlbumRepository
import fr.cassettelabs.cassette.domain.repositories.PlaybackRepository
import fr.cassettelabs.cassette.domain.repositories.PlaylistRepository
import fr.cassettelabs.cassette.domain.repositories.ServerConfigurationRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule =
    module {
        single<CassetteDatabase> {
            get<DatabaseBuilderFactory>()
                .create()
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
        single<ServerConfigurationDao> { get<CassetteDatabase>().serverConfigurationDao() }
        single<AlbumDao> { get<CassetteDatabase>().albumDao() }
        single<TrackDao> { get<CassetteDatabase>().trackDao() }
        single<PlaylistDao> { get<CassetteDatabase>().playlistDao() }
        single<PlaybackQueueDao> { get<CassetteDatabase>().playbackQueueDao() }

        singleOf(::KtorClientProviderImpl)
        single<HttpClient> { get<KtorClientProviderImpl>().getClient() }
        singleOf(::CassetteRequestDefaultsPluginProvider)
        singleOf(::CassetteRequestAuthenticationPluginProvider)

        singleOf(::AlbumRemoteDataSourceImpl)
        singleOf(::PlaylistRemoteDataSourceImpl)

        singleOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
        singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
        singleOf(::PlaylistRepositoryImpl) bind PlaylistRepository::class
        singleOf(::PlaybackRepositoryImpl) bind PlaybackRepository::class
    }
