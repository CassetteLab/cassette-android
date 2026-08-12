package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingServerConfiguration

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_title
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationScreenContent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.core.ServerConfigurationConnectBottomBar
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun OnBoardingServerConfigurationScreen(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            CassetteTopAppBar(
                title = Res.string.server_configuration_title,
            )
        },
        bottomBar = {
            ServerConfigurationConnectBottomBar(
                uiState = uiState,
                onConnectClick = { onEvent(ServerConfigurationEvent.OnConnectClicked) },
            )
        },
    ) { innerPadding ->
        ServerConfigurationScreenContent(
            contentPadding = innerPadding,
            uiState = uiState,
            onEvent = onEvent,
        )
    }
}

@Composable
@Preview
private fun OnBoardingServerConfigurationScreenPreview() {
    CassetteTheme {
        OnBoardingServerConfigurationScreen(
            uiState = ServerConfigurationUiState(),
            onEvent = {},
        )
    }
}
