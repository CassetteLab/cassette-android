package fr.cassette.cassette.core.di

import fr.cassette.cassette.core.helpers.CipherHelper
import fr.cassette.cassette.core.helpers.implementations.AndroidKeystoreCipherHelperImpl
import fr.cassette.cassette.core.logger.Logger
import fr.cassette.cassette.core.logger.implementations.LogcatLoggerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    factoryOf(::LogcatLoggerImpl) bind Logger::class
    singleOf(::AndroidKeystoreCipherHelperImpl) bind CipherHelper::class
}
