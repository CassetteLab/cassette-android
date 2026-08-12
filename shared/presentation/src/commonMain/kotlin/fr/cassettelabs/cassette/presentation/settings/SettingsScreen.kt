package fr.cassettelabs.cassette.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.settings_title
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsDestinations
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
) {
    LaunchedEffect(Unit){
        onEvent(SettingsEvent.OnAppearing)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.settings_title,
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
                SettingsItemGroup {
                    SettingsDestinations.entries.forEach { setting ->
                        SettingsItem(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                setting.destination?.let { destination ->
                                    onEvent(SettingsEvent.OnDestinationClicked(destination))
                                }
                            },
                            title = stringResource(setting.title),
                            subTitle = stringResource(setting.subTitle),
                            icon = setting.icon,
                            iconShape = setting.iconShape.toShape(),
                            iconForegroundColor = setting.iconForegroundColor,
                            iconBackgroundColor = setting.iconBackgroundColor
                        )
                    }
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
