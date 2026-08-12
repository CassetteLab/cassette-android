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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.settings_application_information_title
import cassette.shared.presentation.generated.resources.settings_build_type_debug
import cassette.shared.presentation.generated.resources.settings_build_type_release
import cassette.shared.presentation.generated.resources.settings_build_type_title
import cassette.shared.presentation.generated.resources.settings_create_issue_subtitle
import cassette.shared.presentation.generated.resources.settings_create_issue_title
import cassette.shared.presentation.generated.resources.settings_github_subtitle
import cassette.shared.presentation.generated.resources.settings_github_title
import cassette.shared.presentation.generated.resources.settings_license_subtitle
import cassette.shared.presentation.generated.resources.settings_license_title
import cassette.shared.presentation.generated.resources.settings_report_bug_subtitle
import cassette.shared.presentation.generated.resources.settings_report_bug_title
import cassette.shared.presentation.generated.resources.settings_version_code_title
import cassette.shared.presentation.generated.resources.settings_version_name_title
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsGenericItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ApplicationInformationScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        onEvent(SettingsEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.settings_application_information_title,
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
                        title = stringResource(Res.string.settings_version_name_title),
                        value = uiState.versionName,
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_version_code_title),
                        value = uiState.versionCode,
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_build_type_title),
                        value =
                            stringResource(
                                if (uiState.isDebugBuild) {
                                    Res.string.settings_build_type_debug
                                } else {
                                    Res.string.settings_build_type_release
                                },
                            ),
                    )
                }
            }
            item {
                SettingsItemGroup {
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_github_title),
                        value = stringResource(Res.string.settings_github_subtitle),
                        onClick = { uriHandler.openUri(GITHUB_URL) },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_license_title),
                        value = stringResource(Res.string.settings_license_subtitle),
                        onClick = { uriHandler.openUri(LICENSE_URL) },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_report_bug_title),
                        value = stringResource(Res.string.settings_report_bug_subtitle),
                        onClick = { uriHandler.openUri(REPORT_BUG_MAILTO) },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.settings_create_issue_title),
                        value = stringResource(Res.string.settings_create_issue_subtitle),
                        onClick = { uriHandler.openUri(CREATE_ISSUE_URL) },
                    )
                }
            }
        }
    }
}

private const val GITHUB_URL = "https://github.com/CassetteLabs/cassette-android"
private const val LICENSE_URL = "https://github.com/CassetteLabs/cassette-android/blob/main/LICENSE"
private const val REPORT_BUG_MAILTO = "mailto:support@getcassette.app?subject=Cassette%20bug%20report"
private const val CREATE_ISSUE_URL = "https://github.com/CassetteLabs/cassette-android/issues/new"

@Composable
@Preview
private fun ApplicationInformationScreenPreview() {
    CassetteTheme {
        ApplicationInformationScreen(
            uiState =
                SettingsUiState(
                    versionName = "1.0.0",
                    versionCode = "1",
                    isDebugBuild = true,
                ),
            onEvent = { },
        )
    }
}
