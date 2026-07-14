package fr.cassette.cassette.core.di

import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.core.logger.implementations.LogcatLoggerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    factoryOf(::LogcatLoggerImpl) bind Logger::class
}
