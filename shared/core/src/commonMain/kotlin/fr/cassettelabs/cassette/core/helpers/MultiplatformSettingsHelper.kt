package fr.cassettelabs.cassette.core.helpers

import com.russhwolf.settings.Settings
import fr.cassettelabs.cassette.core.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MultiplatformSettingsHelper : SettingsHelper {
    private val settings: Settings = Settings()
    private val _themeFlow = MutableStateFlow(loadTheme())
    override val themeFlow: StateFlow<ThemeMode> = _themeFlow

    override suspend fun setTheme(theme: ThemeMode) {
        settings.putString(KEY_THEME_MODE, theme.preferenceKey)
        _themeFlow.value = theme
    }

    private fun loadTheme(): ThemeMode {
        val key = settings.getStringOrNull(KEY_THEME_MODE) ?: return ThemeMode.System
        return ThemeMode.entries.firstOrNull { it.preferenceKey == key } ?: ThemeMode.System
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
