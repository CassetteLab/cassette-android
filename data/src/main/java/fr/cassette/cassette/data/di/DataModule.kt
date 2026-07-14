package fr.cassette.cassette.data.di

import androidx.room.Room
import fr.cassette.cassette.data.local.CassetteDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CassetteDatabase::class.java,
            "cassette.db",
        ).build()
    }

    single { get<CassetteDatabase>().serverConfigurationDao() }
}
