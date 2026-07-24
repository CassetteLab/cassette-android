package fr.cassettelabs.cassette.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fr.cassettelabs.cassette.data.local.CassetteDatabase
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.local.dao.ServerConfigurationDao
import fr.cassettelabs.cassette.data.remote.ktor.KtorClientProviderImpl
import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestAuthenticationPluginProvider
import fr.cassettelabs.cassette.data.remote.ktor.plugins.CassetteRequestDefaultsPluginProvider
import fr.cassettelabs.cassette.data.repositories.ServerConfigurationRepositoryImpl
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

        singleOf(::KtorClientProviderImpl)
        single<HttpClient> { get<KtorClientProviderImpl>().getClient() }
        singleOf(::CassetteRequestDefaultsPluginProvider)
        singleOf(::CassetteRequestAuthenticationPluginProvider)

        singleOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
    }
