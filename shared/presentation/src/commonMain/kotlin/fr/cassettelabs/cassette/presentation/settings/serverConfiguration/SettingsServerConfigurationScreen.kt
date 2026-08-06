package fr.cassettelabs.cassette.presentation.settings.serverConfiguration

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_settings_title
import cassette.shared.presentation.generated.resources.settings_back
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationScreenContent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.core.ServerConfigurationConnectBottomBar
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsServerConfigurationScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier =
            Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(contentPadding)
                .imePadding(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Text(
                        text = stringResource(Res.string.server_configuration_settings_title),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ServerConfigurationEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.settings_back),
                        )
                    }
                },
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

@Preview
@Composable
private fun SettingsServerConfigurationScreenPreview() {
    CassetteTheme {
        SettingsServerConfigurationScreen(
            uiState = ServerConfigurationUiState(),
            onEvent = { },
        )
    }
}
