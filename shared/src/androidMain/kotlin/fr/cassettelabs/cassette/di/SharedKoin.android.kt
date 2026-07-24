package fr.cassettelabs.cassette.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import fr.cassettelabs.cassette.core.helpers.AndroidApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.AndroidKeystoreCipherHelper
import fr.cassettelabs.cassette.core.helpers.ApplicationInformationHelper
import fr.cassettelabs.cassette.core.helpers.CipherHelper
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import fr.cassettelabs.cassette.data.remote.coverart.AndroidCoverArtProcessor
import fr.cassettelabs.cassette.data.remote.coverart.CoverArtProcessor
import fr.cassettelabs.cassette.data.remote.player.AndroidPlayerEngine
import fr.cassettelabs.cassette.data.remote.player.PlayerEngine
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

@Composable
actual fun platformModule(): Module {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        module {
            single { DatabaseBuilderFactory(context) }
            single { AndroidApplicationInformationHelper(context) } bind ApplicationInformationHelper::class
            single { AndroidKeystoreCipherHelper() } bind CipherHelper::class
            single { AndroidCoverArtProcessor(File(context.cacheDir, "cover_art").apply { mkdirs() }) } bind CoverArtProcessor::class
            single { AndroidPlayerEngine(context) } bind PlayerEngine::class
        }
    }
}
