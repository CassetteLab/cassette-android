package fr.cassette.cassette.presentation.core.serverConfiguration.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState

@Composable
internal fun CustomHeadersSection(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    FormSection(titleRes = R.string.server_configuration_custom_headers_section_title) {
        uiState.customHeaders.forEach { header ->
            CustomHeaderRow(header = header, onEvent = onEvent)
        }

        TextButton(onClick = { onEvent(ServerConfigurationEvent.OnAddHeaderClicked) }) {
            Text(text = stringResource(R.string.server_configuration_add_header))
        }

        Text(
            text = stringResource(R.string.server_configuration_custom_headers_footer),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
