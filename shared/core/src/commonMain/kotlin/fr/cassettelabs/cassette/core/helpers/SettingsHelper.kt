package fr.cassettelabs.cassette.core.helpers

import fr.cassettelabs.cassette.core.theme.ThemeMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsHelper {
    val themeFlow: StateFlow<ThemeMode>
    suspend fun setTheme(theme: ThemeMode)
}
