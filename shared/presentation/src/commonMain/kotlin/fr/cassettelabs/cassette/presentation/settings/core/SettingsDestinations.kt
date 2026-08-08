package fr.cassettelabs.cassette.presentation.settings.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Filter
import androidx.compose.material.icons.rounded.ImageAspectRatio
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.graphics.shapes.RoundedPolygon
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.settings_application_information_subtitle
import cassette.shared.presentation.generated.resources.settings_application_information_title
import cassette.shared.presentation.generated.resources.settings_configuration_subtitle
import cassette.shared.presentation.generated.resources.settings_configuration_title
import cassette.shared.presentation.generated.resources.settings_local_storage_subtitle
import cassette.shared.presentation.generated.resources.settings_local_storage_title
import cassette.shared.presentation.generated.resources.settings_logs_subtitle
import cassette.shared.presentation.generated.resources.settings_logs_title
import fr.cassettelabs.cassette.presentation.core.navigation.Screens
import org.jetbrains.compose.resources.StringResource

private val ApplicationInformationForeground = Color(0xFF5B21B6)
private val ApplicationInformationBackground = Color(0xFFEDE9FE)
private val LocalStorageForeground = Color(0xFF0369A1)
private val LocalStorageBackground = Color(0xFFE0F2FE)
private val LogsForeground = Color(0xFFB45309)
private val LogsBackground = Color(0xFFFEF3C7)
private val ConfigurationForeground = Color(0xFF047857)
private val ConfigurationBackground = Color(0xFFD1FAE5)

internal enum class SettingsDestinations(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: ImageVector,
    val iconShape: RoundedPolygon,
    val iconForegroundColor: Color,
    val iconBackgroundColor: Color,
    val destination: Screens?
) {
    ApplicationInformation(
        title = Res.string.settings_application_information_title,
        subTitle = Res.string.settings_application_information_subtitle,
        icon = Icons.Rounded.Info,
        iconShape = MaterialShapes.PixelCircle,
        iconForegroundColor = ApplicationInformationForeground,
        iconBackgroundColor = ApplicationInformationBackground,
        destination = Screens.SettingsApplicationInformation
    ),
    LocalStorage(
        title = Res.string.settings_local_storage_title,
        subTitle = Res.string.settings_local_storage_subtitle,
        icon = Icons.Rounded.Storage,
        iconShape = MaterialShapes.Cookie7Sided,
        iconForegroundColor = LocalStorageForeground,
        iconBackgroundColor = LocalStorageBackground,
        destination = null
    ),
    Logs(
        title = Res.string.settings_logs_title,
        subTitle = Res.string.settings_logs_subtitle,
        icon = Icons.Rounded.Filter,
        iconShape = MaterialShapes.VerySunny,
        iconForegroundColor = LogsForeground,
        iconBackgroundColor = LogsBackground,
        destination = Screens.SettingsLogs
    ),
    Configuration(
        title = Res.string.settings_configuration_title,
        subTitle = Res.string.settings_configuration_subtitle,
        icon = Icons.Rounded.ImageAspectRatio,
        iconShape = MaterialShapes.Clover4Leaf,
        iconForegroundColor = ConfigurationForeground,
        iconBackgroundColor = ConfigurationBackground,
        destination = Screens.SettingsConfiguration
    )
}
