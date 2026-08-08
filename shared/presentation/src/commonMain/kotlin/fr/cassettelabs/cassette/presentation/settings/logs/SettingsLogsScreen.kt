package fr.cassettelabs.cassette.presentation.settings.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import cassette.shared.presentation.generated.resources.settings_logs_placeholder_description
import cassette.shared.presentation.generated.resources.settings_logs_placeholder_title
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
            if (uiState.isPlaceholderVisible) {
                item {
                    SettingsItemGroup {
                        SettingsGenericItem(
                            title = stringResource(Res.string.settings_logs_placeholder_title),
                            description = stringResource(Res.string.settings_logs_placeholder_description),
                        )
                    }
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
