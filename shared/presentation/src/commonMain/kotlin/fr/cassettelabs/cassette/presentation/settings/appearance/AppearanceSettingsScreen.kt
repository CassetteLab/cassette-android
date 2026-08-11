package fr.cassettelabs.cassette.presentation.settings.appearance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.appearance_theme_dark
import cassette.shared.presentation.generated.resources.appearance_theme_light
import cassette.shared.presentation.generated.resources.appearance_theme_system
import cassette.shared.presentation.generated.resources.appearance_theme_title
import cassette.shared.presentation.generated.resources.appearance_title
import fr.cassettelabs.cassette.core.theme.ThemeMode
import fr.cassettelabs.cassette.presentation.core.CassetteTopAppBar
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.settings.core.SettingsGenericItem
import fr.cassettelabs.cassette.presentation.settings.core.SettingsItemGroup
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AppearanceSettingsScreen(
    contentPadding: PaddingValues = PaddingValues(),
    uiState: AppearanceSettingsUiState,
    onEvent: (AppearanceSettingsEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CassetteTopAppBar(
                title = Res.string.appearance_title,
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
                        ),
                    )
                    .plus(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsItemGroup {
                    SettingsGenericItem(
                        title = stringResource(Res.string.appearance_theme_system),
                        description = stringResource(Res.string.appearance_theme_title),
                        value = if (uiState.themeMode == ThemeMode.System) "✓" else null,
                        onClick = {
                            onEvent(AppearanceSettingsEvent.OnThemeSelected(ThemeMode.System))
                        },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.appearance_theme_light),
                        description = stringResource(Res.string.appearance_theme_title),
                        value = if (uiState.themeMode == ThemeMode.Light) "✓" else null,
                        onClick = {
                            onEvent(AppearanceSettingsEvent.OnThemeSelected(ThemeMode.Light))
                        },
                    )
                    SettingsGenericItem(
                        title = stringResource(Res.string.appearance_theme_dark),
                        description = stringResource(Res.string.appearance_theme_title),
                        value = if (uiState.themeMode == ThemeMode.Dark) "✓" else null,
                        onClick = {
                            onEvent(AppearanceSettingsEvent.OnThemeSelected(ThemeMode.Dark))
                        },
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun AppearanceSettingsScreenPreview() {
    CassetteTheme {
        AppearanceSettingsScreen(
            uiState = AppearanceSettingsUiState(),
            onEvent = {},
        )
    }
}
