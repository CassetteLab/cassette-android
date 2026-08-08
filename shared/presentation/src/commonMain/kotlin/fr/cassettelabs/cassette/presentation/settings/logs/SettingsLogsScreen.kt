package fr.cassettelabs.cassette.presentation.settings.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.settings_logs_empty_description
import cassette.shared.presentation.generated.resources.settings_logs_empty_title
import cassette.shared.presentation.generated.resources.settings_logs_export
import cassette.shared.presentation.generated.resources.settings_logs_export_failed
import cassette.shared.presentation.generated.resources.settings_logs_exporting
import cassette.shared.presentation.generated.resources.settings_logs_loading
import cassette.shared.presentation.generated.resources.settings_logs_title
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsGenericItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsLogsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsLogsUiState,
    onEvent: (SettingsLogsEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(SettingsLogsEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.settings_logs_title,
                onBackClicked = { onEvent(SettingsLogsEvent.OnBackClicked) },
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
            if (uiState.isLoading) {
                item {
                    SettingsItemGroup {
                        SettingsGenericItem(
                            title = stringResource(Res.string.settings_logs_loading),
                        )
                    }
                }
            } else if (uiState.logFiles.isEmpty()) {
                item {
                    SettingsItemGroup {
                        SettingsGenericItem(
                            title = stringResource(Res.string.settings_logs_empty_title),
                            description = stringResource(Res.string.settings_logs_empty_description),
                        )
                    }
                }
            } else {
                item {
                    SettingsItemGroup {
                        uiState.logFiles.forEach { logFile ->
                            SettingsGenericItem(
                                title = logFile.name,
                                value = logFile.formattedSize,
                            )
                        }
                    }
                }
            }

            item {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = !uiState.isLoading && !uiState.isExporting && uiState.logFiles.isNotEmpty(),
                    onClick = { onEvent(SettingsLogsEvent.OnExportLogDirectoryClicked) },
                    isLoading = uiState.isExporting,
                    text = stringResource(Res.string.settings_logs_export),
                )
            }

            if (uiState.exportFailed) {
                item {
                    Text(
                        text = stringResource(Res.string.settings_logs_export_failed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun SettingsLogsScreenPreview() {
    CassetteTheme {
        SettingsLogsScreen(
            uiState = SettingsLogsUiState(),
            onEvent = { },
        )
    }
}
