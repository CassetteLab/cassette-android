package fr.cassette.cassette.presentation.core.serverConfiguration

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.cassette.cassette.presentation.R

internal sealed interface ServerConfigurationError {
    data object ConnectionFailed : ServerConfigurationError
}

@Composable
internal fun ServerConfigurationError.asString(): String =
    when (this) {
        ServerConfigurationError.ConnectionFailed -> stringResource(R.string.server_configuration_connection_failed)
    }
