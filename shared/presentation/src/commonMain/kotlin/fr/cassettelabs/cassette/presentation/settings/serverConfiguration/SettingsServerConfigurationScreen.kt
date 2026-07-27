package fr.cassettelabs.cassette.presentation.settings.serverConfiguration

import cassette.shared.presentation.generated.resources.server_configuration_settings_title
import cassette.shared.presentation.generated.resources.settings_back
import cassette.shared.presentation.generated.resources.server_configuration_connect_and_save
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationScreenContent
import fr.cassettelabs.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsServerConfigurationScreen(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier =
            Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .imePadding(),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Text(
                        text = stringResource(Res.string.server_configuration_settings_title),
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
            BottomAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEvent(ServerConfigurationEvent.OnConnectClicked)
                    },
                    isEnabled = uiState.canSubmit,
                    isLoading = uiState.isLoading,
                    text = stringResource(Res.string.server_configuration_connect_and_save),
                )
            }
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
