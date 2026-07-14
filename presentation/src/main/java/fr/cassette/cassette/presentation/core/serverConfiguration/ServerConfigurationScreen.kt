package fr.cassette.cassette.presentation.core.serverConfiguration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.serverConfiguration.core.CredentialsSection
import fr.cassette.cassette.presentation.core.serverConfiguration.core.CustomHeadersSection
import fr.cassette.cassette.presentation.core.serverConfiguration.core.ServerSection
import fr.cassette.cassette.presentation.ui.theme.CassetteTheme

@Composable
internal fun ServerConfigurationScreen(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = stringResource(R.string.server_configuration_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
            )

            ServerSection(uiState = uiState, onEvent = onEvent)
            CredentialsSection(uiState = uiState, onEvent = onEvent)
            CustomHeadersSection(uiState = uiState, onEvent = onEvent)

            Button(
                onClick = { onEvent(ServerConfigurationEvent.OnConnectClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.canSubmit,
                shape = RoundedCornerShape(18.dp),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.server_configuration_connecting))
                } else {
                    Text(text = stringResource(R.string.server_configuration_connect_and_save))
                }
            }
        }
    }
}

@Composable
@Preview
private fun ServerConfigurationScreenPreview() {
    CassetteTheme {
        ServerConfigurationScreen(
            uiState = ServerConfigurationUiState(
                serverUrl = "https://music.example.com",
                username = "cassette",
                customHeaders = listOf(ServerConfigurationHeaderUiState(id = 0L, name = "CF-Access-Client-Id")),
            ),
            onEvent = {},
        )
    }
}
