package fr.cassette.cassette.presentation.main.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import fr.cassette.cassette.presentation.R

internal enum class MainTab(
    val labelRes: Int,
    val iconRes: ImageVector
) {
    Home(R.string.home_title, Icons.Default.Home),
    Settings(R.string.settings_title, Icons.Default.Settings),
}