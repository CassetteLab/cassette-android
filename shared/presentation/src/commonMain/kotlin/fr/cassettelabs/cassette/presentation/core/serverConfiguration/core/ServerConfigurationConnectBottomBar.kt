package fr.cassettelabs.cassette.presentation.core.serverConfiguration.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_connect_and_save
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ServerConfigurationConnectBottomBar(
    uiState: ServerConfigurationUiState,
    onConnectClick: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onConnectClick,
            isEnabled = uiState.canSubmit,
            isLoading = uiState.isLoading,
            text = stringResource(Res.string.server_configuration_connect_and_save),
        )
    }
}
