package fr.cassettelabs.cassette.core.di

import fr.cassettelabs.cassette.core.coroutines.CoroutineDispatchers
import fr.cassettelabs.cassette.core.coroutines.DefaultCoroutineDispatchers
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.core.logger.PlatformLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule =
    module {
        includes(corePlatformModule)

        factoryOf(::PlatformLogger) bind Logger::class
        singleOf(::DefaultCoroutineDispatchers) bind CoroutineDispatchers::class
    }

expect val corePlatformModule: Module
