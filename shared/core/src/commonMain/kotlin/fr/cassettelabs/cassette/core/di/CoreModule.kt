package fr.cassettelabs.cassette.core.di

import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.core.logger.PlatformLogger
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule =
    module {
        factoryOf(::PlatformLogger) bind Logger::class
    }
