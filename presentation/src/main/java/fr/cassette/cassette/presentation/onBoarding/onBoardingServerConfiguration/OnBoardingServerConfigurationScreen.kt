package fr.cassette.cassette.presentation.onBoarding.onBoardingServerConfiguration

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.PrimaryButton
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationEvent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationScreenContent
import fr.cassette.cassette.presentation.core.serverConfiguration.ServerConfigurationUiState
import fr.cassette.cassette.presentation.core.theme.CassetteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OnBoardingServerConfigurationScreen(
    uiState: ServerConfigurationUiState,
    onEvent: (ServerConfigurationEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.server_configuration_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ServerConfigurationEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back)
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEvent(ServerConfigurationEvent.OnConnectClicked)
                    },
                    isEnabled = uiState.canSubmit,
                    isLoading = uiState.isLoading,
                    text = stringResource(R.string.server_configuration_connect_and_save)
                )
            }
        }
    ) { innerPadding ->
        ServerConfigurationScreenContent(
            contentPadding = innerPadding,
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

@PreviewLightDark
@Composable
private fun OnBoardingServerConfigurationScreenPreview(){
    CassetteTheme {
        OnBoardingServerConfigurationScreen(
            uiState = ServerConfigurationUiState(),
            onEvent = { }
        )
    }
}