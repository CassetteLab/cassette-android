package fr.cassettelabs.cassette.presentation.settings

import cassette.shared.presentation.generated.resources.settings_title
import cassette.shared.presentation.generated.resources.settings_app_name
import cassette.shared.presentation.generated.resources.settings_app_tagline
import cassette.shared.presentation.generated.resources.settings_app_version_format
import cassette.shared.presentation.generated.resources.settings_app_build_type_format
import cassette.shared.presentation.generated.resources.settings_build_type_debug
import cassette.shared.presentation.generated.resources.settings_build_type_release
import cassette.shared.presentation.generated.resources.settings_server_section_title
import cassette.shared.presentation.generated.resources.settings_logout_description
import cassette.shared.presentation.generated.resources.settings_logout_title
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsActionRow
import fr.cassettelabs.cassette.presentation.settings.core.SettingsAppInfoCard
import fr.cassettelabs.cassette.presentation.settings.core.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit){
        onEvent(SettingsEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                        text = stringResource(Res.string.settings_title),
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding =
                contentPadding
                    .plus(
                    PaddingValues(
                        vertical = 20.dp,
                        horizontal = 16.dp,
                    )
                )
                .plus(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsAppInfoCard(
                    appName = stringResource(Res.string.settings_app_name),
                    tagline = stringResource(Res.string.settings_app_tagline),
                    versionLabel = stringResource(Res.string.settings_app_version_format, uiState.versionName, uiState.versionCode),
                    buildTypeLabel =
                        stringResource(
                            Res.string.settings_app_build_type_format,
                            stringResource(
                                if (uiState.isDebugBuild) {
                                    Res.string.settings_build_type_debug
                                } else {
                                    Res.string.settings_build_type_release
                                },
                            ),
                        ),
                )
            }
            item {
                SettingsSection(title = stringResource(Res.string.settings_server_section_title)) {
                    SettingsActionRow(
                        title = stringResource(Res.string.settings_logout_title),
                        description = stringResource(Res.string.settings_logout_description),
                        leadingIcon = Icons.AutoMirrored.Rounded.Logout,
                        enabled = !uiState.isLoggingOut,
                        leadingIconTint = MaterialTheme.colorScheme.error,
                        showNavigationIndicator = false,
                        onClick = { onEvent(SettingsEvent.OnLogoutClicked) },
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun SettingsScreenPreview() {
    CassetteTheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            onEvent = {},
        )
    }
}
