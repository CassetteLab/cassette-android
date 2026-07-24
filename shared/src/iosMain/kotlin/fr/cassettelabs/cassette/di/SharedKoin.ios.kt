package fr.cassettelabs.cassette.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.IosApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.IosCipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.coverart.IosCoverArtProcessor
import fr.cassettelabs.cassette.data.remote.player.IosPlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

@Composable
actual fun platformModule(): Module =
    remember {
        module {
            single { DatabaseBuilderFactory() }
            single { IosApplicationInformationHelper() } bind ApplicationInformationHelper::class
            single { IosCipherHelper() } bind CipherHelper::class
            single { IosCoverArtProcessor() } bind CoverArtProcessor::class
            single { IosPlayerEngine() } bind PlayerEngine::class
        }
    }
