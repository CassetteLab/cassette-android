package fr.cassette.cassette.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
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
import fr.cassette.cassette.presentation.settings.core.SettingsAppInfoCard
import fr.cassette.cassette.presentation.settings.core.SettingsSection

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
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                    )
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    top = contentPadding.calculateTopPadding() + 20.dp,
                    end = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 20.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsAppInfoCard(
                    appName = stringResource(R.string.settings_app_name),
                    tagline = stringResource(R.string.settings_app_tagline),
                    versionLabel = stringResource(R.string.settings_app_version_format, uiState.versionName, uiState.versionCode),
                    buildTypeLabel =
                        stringResource(
                            R.string.settings_app_build_type_format,
                            stringResource(
                                if (uiState.isDebugBuild) {
                                    R.string.settings_build_type_debug
                                } else {
                                    R.string.settings_build_type_release
                                },
                            ),
                        ),
                )
            }
            item {
                SettingsSection(title = stringResource(R.string.settings_server_section_title)) {
                    SettingsActionRow(
                        title = stringResource(R.string.settings_server_configuration_title),
                        description = stringResource(R.string.settings_server_configuration_description),
                        leadingIcon = Icons.Rounded.Storage,
                        onClick = { onEvent(SettingsEvent.OnServerConfigurationClicked) },
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
