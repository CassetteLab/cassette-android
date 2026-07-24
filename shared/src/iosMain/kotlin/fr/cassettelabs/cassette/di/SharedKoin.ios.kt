package fr.cassettelabs.cassette.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fr.cassettelabs.cassette.data.local.DatabaseBuilderFactory
import org.koin.core.module.Module
import org.koin.dsl.module

@Composable
actual fun platformModule(): Module =
    remember {
        module {
            single { DatabaseBuilderFactory() }
        }
    }
