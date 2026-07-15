package fr.cassette.cassette.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.settings.core.SettingsActionRow
import fr.cassette.cassette.presentation.settings.core.SettingsInformationRow
import fr.cassette.cassette.presentation.settings.core.SettingsSection
import fr.cassette.cassette.presentation.settings.core.SettingsSwitchRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(
                        text = stringResource(R.string.settings_title)
                    )
                }
            )
        }
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
            item {
                SettingsSection(title = stringResource(R.string.settings_about_section_title)) {
                    SettingsInformationRow(
                        title = stringResource(R.string.settings_version_name_title),
                        value = uiState.versionName,
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    SettingsInformationRow(
                        title = stringResource(R.string.settings_version_code_title),
                        value = uiState.versionCode,
                    )
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    SettingsInformationRow(
                        title = stringResource(R.string.settings_build_type_title),
                        value = stringResource(
                            if (uiState.isDebugBuild) {
                                R.string.settings_build_type_debug
                            } else {
                                R.string.settings_build_type_release
                            }
                        ),
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
