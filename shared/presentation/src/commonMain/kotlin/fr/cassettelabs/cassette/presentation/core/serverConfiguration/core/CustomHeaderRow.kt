package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

import cassette.shared.presentation.generated.resources.server_configuration_header_name_label
import cassette.shared.presentation.generated.resources.server_configuration_header_name_placeholder
import cassette.shared.presentation.generated.resources.server_configuration_header_name_error
import cassette.shared.presentation.generated.resources.server_configuration_header_value_label
import cassette.shared.presentation.generated.resources.server_configuration_header_value_hide
import cassette.shared.presentation.generated.resources.server_configuration_header_value_show
import cassette.shared.presentation.generated.resources.server_configuration_header_value_error
import cassette.shared.presentation.generated.resources.server_configuration_remove_header
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationHeaderUiState

@Composable
internal fun CustomHeaderRow(
    header: ServerConfigurationHeaderUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = header.name,
            onValueChange = { onEvent(ServerConfigurationEvent.OnHeaderNameChanged(header.id, it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.server_configuration_header_name_label)) },
            placeholder = { Text(text = stringResource(Res.string.server_configuration_header_name_placeholder)) },
            isError = !header.isNameValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
        )
        if (!header.isNameValid) {
            Text(
                text = stringResource(Res.string.server_configuration_header_name_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        OutlinedTextField(
            value = header.value,
            onValueChange = { onEvent(ServerConfigurationEvent.OnHeaderValueChanged(header.id, it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.server_configuration_header_value_label)) },
            isError = !header.isValueValid,
            visualTransformation = if (header.isValueVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
            trailingIcon = {
                TextButton(
                    onClick = {
                        onEvent(
                            ServerConfigurationEvent.OnHeaderValueVisibilityChanged(
                                id = header.id,
                                isVisible = !header.isValueVisible,
                            ),
                        )
                    },
                ) {
                    Text(
                        text =
                            stringResource(
                                if (header.isValueVisible) {
                                    Res.string.server_configuration_header_value_hide
                                } else {
                                    Res.string.server_configuration_header_value_show
                                },
                            ),
                    )
                }
            },
        )
        if (!header.isValueValid) {
            Text(
                text = stringResource(Res.string.server_configuration_header_value_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { onEvent(ServerConfigurationEvent.OnRemoveHeaderClicked(header.id)) }) {
                Text(text = stringResource(Res.string.server_configuration_remove_header))
            }
        }
    }
}
