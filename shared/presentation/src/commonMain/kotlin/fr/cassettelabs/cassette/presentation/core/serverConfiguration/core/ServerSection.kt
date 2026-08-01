package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

import cassette.shared.presentation.generated.resources.server_configuration_server_section_title
import cassette.shared.presentation.generated.resources.server_configuration_server_url_label
import cassette.shared.presentation.generated.resources.server_configuration_server_url_placeholder
import cassette.shared.presentation.generated.resources.server_configuration_server_url_error
import cassette.shared.presentation.generated.resources.server_configuration_http_warning
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState

@Composable
internal fun ServerSection(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    FormSection(titleRes = Res.string.server_configuration_server_section_title) {
        OutlinedTextField(
            value = uiState.serverUrl,
            onValueChange = { onEvent(ServerConfigurationEvent.OnServerUrlChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.server_configuration_server_url_label)) },
            placeholder = { Text(text = stringResource(Res.string.server_configuration_server_url_placeholder)) },
            isError = !uiState.isUrlValid,
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
        )
        if (!uiState.isUrlValid) {
            Text(
                text = stringResource(Res.string.server_configuration_server_url_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (uiState.isHttp) {
            Text(
                text = stringResource(Res.string.server_configuration_http_warning),
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
