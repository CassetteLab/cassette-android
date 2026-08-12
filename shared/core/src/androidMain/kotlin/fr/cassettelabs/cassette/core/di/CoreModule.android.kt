package fr.cassettelabs.cassette.core.di

import fr.cassettelabs.cassette.core.helpers.AndroidApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.AndroidKeystoreCipherHelper
import fr.cassettelabs.cassette.core.helpers.AndroidLogFilesHelper
import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.LogFilesHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val corePlatformModule: Module
    get() = module {
        singleOf(::AndroidApplicationInformationHelper) bind ApplicationInformationHelper::class
        singleOf(::AndroidKeystoreCipherHelper) bind CipherHelper::class
        singleOf(::AndroidLogFilesHelper) bind LogFilesHelper::class
    }
