package fr.cassettelabs.cassette.data.di

import android.content.Context
import fr.cassettelabs.cassette.core.helpers.AndroidKeystoreCipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.remote.coverart.AndroidCoverArtProcessor
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.player.AndroidPlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val dataPlatformModule: Module
    get() = module {
        singleOf(::DatabaseBuilderFactory)
        single<CoverArtProcessor>{
            val context : Context = get()
            AndroidCoverArtProcessor(
                cacheDir = context.cacheDir,
            )
        }
        singleOf(::AndroidPlayerEngine) bind PlayerEngine::class
    }
