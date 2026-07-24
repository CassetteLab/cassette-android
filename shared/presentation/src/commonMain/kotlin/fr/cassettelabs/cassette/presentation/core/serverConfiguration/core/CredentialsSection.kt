package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_credentials_section_title
import cassette.shared.presentation.generated.resources.server_configuration_password_label
import cassette.shared.presentation.generated.resources.server_configuration_username_label
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CredentialsSection(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    FormSection(title = Res.string.server_configuration_credentials_section_title) {
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { onEvent(ServerConfigurationEvent.OnUsernameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.server_configuration_username_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None),
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onEvent(ServerConfigurationEvent.OnPasswordChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.server_configuration_password_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
    }
}
