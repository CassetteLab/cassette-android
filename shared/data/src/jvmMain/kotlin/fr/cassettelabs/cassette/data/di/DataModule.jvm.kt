package fr.cassettelabs.cassette.data.di

import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.coverart.DesktopCoverArtProcessor
import fr.cassettelabs.cassette.data.remote.player.DesktopPlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val dataPlatformModule: Module
    get() = module {
        singleOf(::DatabaseBuilderFactory)
        singleOf(::DesktopCoverArtProcessor) bind CoverArtProcessor::class
        singleOf(::DesktopPlayerEngine) bind PlayerEngine::class
    }
