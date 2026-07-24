package fr.cassettelabs.cassette.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

@Composable
actual fun platformModule(): Module {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        module {
            single { DatabaseBuilderFactory(context) }
        }
    }
}
