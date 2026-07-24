package fr.cassettelabs.cassette.core.di

import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.NoOpCipherHelper
import fr.cassettelabs.cassette.core.logger.Logger
import fr.cassettelabs.cassette.core.logger.NoOpLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule =
    module {
        singleOf(::NoOpCipherHelper) bind CipherHelper::class
        singleOf(::NoOpLogger) bind Logger::class
    }
