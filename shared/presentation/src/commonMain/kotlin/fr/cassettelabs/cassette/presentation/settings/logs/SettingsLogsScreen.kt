package fr.cassettelabs.cassette.presentation.settings.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.settings_back
import cassette.shared.presentation.generated.resources.settings_logs_empty_description
import cassette.shared.presentation.generated.resources.settings_logs_empty_title
import cassette.shared.presentation.generated.resources.settings_logs_export
import cassette.shared.presentation.generated.resources.settings_logs_export_failed
import cassette.shared.presentation.generated.resources.settings_logs_exporting
import cassette.shared.presentation.generated.resources.settings_logs_loading
import cassette.shared.presentation.generated.resources.settings_logs_title
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsGenericItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsLogsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsLogsUiState,
    onEvent: (SettingsLogsEvent) -> Unit,
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        onEvent(SettingsLogsEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                title = {
                    Text(
                        text = stringResource(Res.string.settings_logs_title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SettingsLogsEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.settings_back),
                        )
                    }
                },
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
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && !uiState.isExporting && uiState.logFiles.isNotEmpty(),
                    onClick = { onEvent(SettingsLogsEvent.OnExportLogDirectoryClicked) },
                ) {
                    Text(
                        text =
                            stringResource(
                                if (uiState.isExporting) {
                                    Res.string.settings_logs_exporting
                                } else {
                                    Res.string.settings_logs_export
                                },
                            ),
                    )
                }
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
