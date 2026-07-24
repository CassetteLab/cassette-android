package fr.cassettelabs.cassette

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(hasValidServerConfiguration: Boolean) =
    ComposeUIViewController {
        App(hasValidServerConfiguration = hasValidServerConfiguration)
    }
