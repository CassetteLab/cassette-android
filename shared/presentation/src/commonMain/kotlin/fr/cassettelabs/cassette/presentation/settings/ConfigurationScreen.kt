package fr.cassettelabs.cassette.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.server_configuration_settings_title
import cassette.shared.presentation.generated.resources.settings_configuration_server_subtitle
import cassette.shared.presentation.generated.resources.settings_configuration_title
import cassette.shared.presentation.generated.resources.settings_logout_description
import cassette.shared.presentation.generated.resources.settings_logout_title
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsGenericItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConfigurationScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.settings_configuration_title,
                onBackClicked = { onEvent(SettingsEvent.OnBackClicked) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding =
                contentPadding
                    .plus(PaddingValues(vertical = 20.dp, horizontal = 16.dp))
                    .plus(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsItemGroup {
                    SettingsGenericItem(
                        title = stringResource(Res.string.server_configuration_settings_title),
                        description = stringResource(Res.string.settings_configuration_server_subtitle),
                        onClick = {
                            onEvent(SettingsEvent.OnDestinationClicked(Screens.SettingsServerConfiguration))
                        },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_logout_title),
                        description = stringResource(Res.string.settings_logout_description),
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = {
                            if (!uiState.isLoggingOut) {
                                onEvent(SettingsEvent.OnLogoutClicked)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun ConfigurationScreenPreview() {
    CassetteTheme {
        ConfigurationScreen(
            uiState = SettingsUiState(),
            onEvent = { },
        )
    }
}
