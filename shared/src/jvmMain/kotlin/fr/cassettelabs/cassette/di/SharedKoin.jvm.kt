package fr.cassettelabs.cassette.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.core.helpers.DesktopApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.JvmCipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.coverart.DesktopCoverArtProcessor
import fr.cassettelabs.cassette.data.remote.player.DesktopPlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

@Composable
actual fun platformModule(): Module =
    remember {
        module {
            single { DatabaseBuilderFactory() }
            single { DesktopApplicationInformationHelper() } bind ApplicationInformationHelper::class
            single { JvmCipherHelper() } bind CipherHelper::class
            single { DesktopCoverArtProcessor() } bind CoverArtProcessor::class
            single { DesktopPlayerEngine() } bind PlayerEngine::class
        }
    }
