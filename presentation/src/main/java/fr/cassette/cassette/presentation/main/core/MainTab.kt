package fr.cassette.cassette.presentation.main.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.navigation.Screens

internal enum class MainTab(
    val labelRes: Int,
    val iconRes: ImageVector,
    val destination: Screens
) {
    Home(R.string.home_title, iconRes = Icons.Default.Album, destination = Screens.Home),
    Settings(R.string.settings_title, iconRes = Icons.Default.Settings, destination = Screens.Settings),
}