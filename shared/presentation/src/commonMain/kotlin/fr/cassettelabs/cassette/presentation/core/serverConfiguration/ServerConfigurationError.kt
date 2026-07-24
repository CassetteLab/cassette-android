package fr.cassettelabs.cassette.presentation.core.serverConfiguration

import cassette.shared.presentation.generated.resources.server_configuration_connection_failed
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

internal sealed interface ServerConfigurationError {
    data object ConnectionFailed : ServerConfigurationError
}

@Composable
internal fun ServerConfigurationError.asString(): String =
    when (this) {
        ServerConfigurationError.ConnectionFailed -> stringResource(Res.string.server_configuration_connection_failed)
    }
