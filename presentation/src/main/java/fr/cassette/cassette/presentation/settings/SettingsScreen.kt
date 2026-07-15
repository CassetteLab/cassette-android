package fr.cassette.cassette.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.settings.core.SettingsActionRow
import fr.cassette.cassette.presentation.settings.core.SettingsSection
import fr.cassette.cassette.presentation.settings.core.SettingsSwitchRow

@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = contentPadding.calculateTopPadding() + 20.dp,
                end = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = { onEvent(SettingsEvent.OnBackClicked) },
                    ) {
                        Text(text = stringResource(R.string.settings_back))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = stringResource(R.string.settings_subtitle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.settings_server_section_title)) {
                    SettingsActionRow(
                        title = stringResource(R.string.settings_server_configuration_title),
                        description = stringResource(R.string.settings_server_configuration_description),
                        onClick = { onEvent(SettingsEvent.OnServerConfigurationClicked) },
                    )
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.settings_playback_section_title)) {
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_wifi_only_downloads_title),
                        description = stringResource(R.string.settings_wifi_only_downloads_description),
                        checked = uiState.isWifiOnlyDownloadsEnabled,
                        onCheckedChange = { isEnabled ->
                            onEvent(SettingsEvent.OnWifiOnlyDownloadsChanged(isEnabled))
                        },
                    )
                }
            }
            item {
                SettingsSection(title = stringResource(R.string.settings_notifications_section_title)) {
                    SettingsSwitchRow(
                        title = stringResource(R.string.settings_notifications_title),
                        description = stringResource(R.string.settings_notifications_description),
                        checked = uiState.areNotificationsEnabled,
                        onCheckedChange = { isEnabled ->
                            onEvent(SettingsEvent.OnNotificationsChanged(isEnabled))
                        },
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun SettingsScreenPreview() {
    CassetteTheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            onEvent = {},
        )
    }
}
