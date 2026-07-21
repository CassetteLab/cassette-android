package fr.cassette.cassette.presentation.core.serverConfiguration.core

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationHeaderUiState

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
            label = { Text(text = stringResource(R.string.server_configuration_header_name_label)) },
            placeholder = { Text(text = stringResource(R.string.server_configuration_header_name_placeholder)) },
            isError = !header.isNameValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
        )
        if (!header.isNameValid) {
            Text(
                text = stringResource(R.string.server_configuration_header_name_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        OutlinedTextField(
            value = header.value,
            onValueChange = { onEvent(ServerConfigurationEvent.OnHeaderValueChanged(header.id, it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.server_configuration_header_value_label)) },
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
                                    R.string.server_configuration_header_value_hide
                                } else {
                                    R.string.server_configuration_header_value_show
                                },
                            ),
                    )
                }
            },
        )
        if (!header.isValueValid) {
            Text(
                text = stringResource(R.string.server_configuration_header_value_error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = { onEvent(ServerConfigurationEvent.OnRemoveHeaderClicked(header.id)) }) {
                Text(text = stringResource(R.string.server_configuration_remove_header))
            }
        }
    }
}
