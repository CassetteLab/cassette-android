package fr.cassette.cassette.data.di

import androidx.room.Room
import fr.cassette.cassette.data.local.CassetteDatabase
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.remote.datasources.AlbumRemoteDataSourceImpl
import fr.cassette.cassette.data.remote.ktor.KtorClientProviderImpl
import fr.cassette.cassette.data.remote.ktor.plugins.CassetteRequestAuthenticationPluginProvider
import fr.cassette.cassette.data.remote.ktor.plugins.CassetteRequestDefaultsPluginProvider
import fr.cassette.cassette.data.repositories.AlbumRepositoryImpl
import fr.cassette.cassette.data.repositories.ServerConfigurationRepositoryImpl
import fr.cassette.cassette.domain.repositories.AlbumRepository
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CassetteDatabase::class.java,
            "cassette.db",
        ).build()
    }

    single<ServerConfigurationDao> { get<CassetteDatabase>().serverConfigurationDao() }

    // Ktor
    singleOf(::KtorClientProviderImpl)
    single<HttpClient> {
        get<KtorClientProviderImpl>().getClient()
    }
    singleOf(::CassetteRequestDefaultsPluginProvider)
    singleOf(::CassetteRequestAuthenticationPluginProvider)

    // Remote data sources
    singleOf(::AlbumRemoteDataSourceImpl)

    // Repositories
    singleOf(::AlbumRepositoryImpl) bind AlbumRepository::class
    singleOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
}
