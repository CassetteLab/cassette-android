package fr.cassettelabs.cassette.core.di

import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.IosApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.IosCipherHelper
import fr.cassettelabs.cassette.core.helpers.IosLogFilesHelper
import fr.cassettelabs.cassette.core.helpers.LogFilesHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val corePlatformModule: Module
    get() = module {
        singleOf(::IosApplicationInformationHelper) bind ApplicationInformationHelper::class
        singleOf(::IosCipherHelper) bind CipherHelper::class
        singleOf(::IosLogFilesHelper) bind LogFilesHelper::class
    }
