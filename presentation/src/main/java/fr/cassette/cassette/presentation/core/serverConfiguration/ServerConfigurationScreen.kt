package fr.cassette.cassette.presentation.core.serverConfiguration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.PrimaryButton
import fr.cassette.cassette.presentation.core.serverConfiguration.core.CredentialsSection
import fr.cassette.cassette.presentation.core.serverConfiguration.core.CustomHeadersSection
import fr.cassette.cassette.presentation.core.serverConfiguration.core.ServerSection
import fr.cassette.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun ServerConfigurationScreenContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr) + 16.dp,
            end = contentPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr) + 16.dp,
            top = contentPadding.calculateTopPadding() + 20.dp,
            bottom = contentPadding.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        uiState.error?.let { error ->
            item {
                Text(
                    text = error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            ServerSection(uiState = uiState, onEvent = onEvent)
        }
        item {
            CredentialsSection(uiState = uiState, onEvent = onEvent)
        }
        item {
            CustomHeadersSection(uiState = uiState, onEvent = onEvent)
        }
    }
}

@Composable
@PreviewLightDark
private fun ServerConfigurationScreenContentPreview() {
    CassetteTheme {
        ServerConfigurationScreenContent(
            uiState = ServerConfigurationUiState(
                serverUrl = "https://music.example.com",
                username = "cassette",
                customHeaders = listOf(ServerConfigurationHeaderUiState(id = 0L, name = "CF-Access-Client-Id")),
            ),
            onEvent = {},
        )
    }
}
