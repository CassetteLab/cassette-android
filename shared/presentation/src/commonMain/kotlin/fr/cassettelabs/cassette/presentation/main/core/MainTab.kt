package fr.cassettelabs.cassette.presentation.main.core

import cassette.shared.presentation.generated.resources.home_title
import cassette.shared.presentation.generated.resources.album_list_tab_title
import cassette.shared.presentation.generated.resources.playlist_list_tab_title
import cassette.shared.presentation.generated.resources.settings_title
import cassette.shared.presentation.generated.resources.starred_tab_title
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import org.jetbrains.compose.resources.StringResource

internal enum class MainTab(
    val labelRes: StringResource,
    val filledIconRes: ImageVector,
    val outlinedIconRes: ImageVector,
    val destination: Screens,
) {
    Home(
        Res.string.home_title,
        filledIconRes = Icons.Filled.Home,
        outlinedIconRes = Icons.Outlined.Home,
        destination = Screens.Home,
    ),
    AlbumList(
        Res.string.album_list_tab_title,
        filledIconRes = Icons.Filled.Album,
        outlinedIconRes = Icons.Outlined.Album,
        destination = Screens.AlbumList,
    ),
    PlaylistList(
        Res.string.playlist_list_tab_title,
        filledIconRes = Icons.AutoMirrored.Filled.PlaylistPlay,
        outlinedIconRes = Icons.AutoMirrored.Outlined.PlaylistPlay,
        destination = Screens.PlaylistList,
    ),
    Starred(
        Res.string.starred_tab_title,
        filledIconRes = Icons.Filled.Star,
        outlinedIconRes = Icons.Outlined.Star,
        destination = Screens.Starred,
    ),
    Settings(
        Res.string.settings_title,
        filledIconRes = Icons.Filled.Settings,
        outlinedIconRes = Icons.Outlined.Settings,
        destination = Screens.Settings,
    ),
}
