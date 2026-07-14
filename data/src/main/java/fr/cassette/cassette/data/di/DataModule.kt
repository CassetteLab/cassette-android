package fr.cassette.cassette.data.di

import androidx.room.Room
import fr.cassette.cassette.data.local.CassetteDatabase
import fr.cassette.cassette.data.local.dao.ServerConfigurationDao
import fr.cassette.cassette.data.repositories.ServerConfigurationRepositoryImpl
import fr.cassette.cassette.domain.repositories.ServerConfigurationRepository
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

    // Repositories
    singleOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
}
