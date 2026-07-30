package fr.cassettelabs.cassette.presentation.core.serverConfiguration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.core.CredentialsSection
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.core.CustomHeadersSection
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.core.ServerSection
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun ServerConfigurationScreenContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding =
            PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr) + 16.dp,
                end = contentPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr) + 16.dp,
                top = contentPadding.calculateTopPadding() + 20.dp,
                bottom = contentPadding.calculateBottomPadding(),
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
@Preview
private fun ServerConfigurationScreenContentPreview() {
    CassetteTheme {
        ServerConfigurationScreenContent(
            uiState =
                ServerConfigurationUiState(
                    serverUrl = "https://music.example.com",
                    username = "cassette",
                    customHeaders = listOf(ServerConfigurationHeaderUiState(id = 0L, name = "CF-Access-Client-Id")),
                ),
            onEvent = {},
        )
    }
}
