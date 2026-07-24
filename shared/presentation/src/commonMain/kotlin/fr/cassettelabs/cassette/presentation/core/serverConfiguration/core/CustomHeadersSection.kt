package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_add_header
import cassette.shared.presentation.generated.resources.server_configuration_custom_headers_footer
import cassette.shared.presentation.generated.resources.server_configuration_custom_headers_section_title
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CustomHeadersSection(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    FormSection(title = Res.string.server_configuration_custom_headers_section_title) {
        uiState.customHeaders.forEach { header ->
            CustomHeaderRow(header = header, onEvent = onEvent)
        }

        TextButton(onClick = { onEvent(ServerConfigurationEvent.OnAddHeaderClicked) }) {
            Text(text = stringResource(Res.string.server_configuration_add_header))
        }

        Text(
            text = stringResource(Res.string.server_configuration_custom_headers_footer),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
