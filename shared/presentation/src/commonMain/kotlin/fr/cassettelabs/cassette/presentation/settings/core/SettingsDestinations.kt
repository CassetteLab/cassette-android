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
import fr.cassettelabs.cassette.presentation.core.theme.CassetteOnPrimary
import fr.cassettelabs.cassette.presentation.core.theme.CassettePrimaryLight
import org.jetbrains.compose.resources.StringResource

internal enum class SettingsDestinations(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: ImageVector,
    val iconShape: RoundedPolygon,
    val iconForegroundColor: Color,
    val iconBackgroundColor: Color,
    val destination: Screens
) {
    ApplicationInformation(
        title = Res.string.settings_application_information_title,
        subTitle = Res.string.settings_application_information_subtitle,
        icon = Icons.Rounded.Info,
        iconShape = MaterialShapes.PixelCircle,
        iconForegroundColor = CassetteOnPrimary,
        iconBackgroundColor = CassettePrimaryLight,
        destination = Screens.PlaybackQueue
    ),
    LocalStorage(
        title = Res.string.settings_local_storage_title,
        subTitle = Res.string.settings_local_storage_subtitle,
        icon = Icons.Rounded.Storage,
        iconShape = MaterialShapes.Cookie7Sided,
        iconForegroundColor = CassetteOnPrimary,
        iconBackgroundColor = CassettePrimaryLight,
        destination = Screens.PlaybackQueue
    ),
    Logs(
        title = Res.string.settings_logs_title,
        subTitle = Res.string.settings_logs_subtitle,
        icon = Icons.Rounded.Filter,
        iconShape = MaterialShapes.VerySunny,
        iconForegroundColor = CassetteOnPrimary,
        iconBackgroundColor = CassettePrimaryLight,
        destination = Screens.PlaybackQueue
    ),
    Configuration(
        title = Res.string.settings_configuration_title,
        subTitle = Res.string.settings_configuration_subtitle,
        icon = Icons.Rounded.ImageAspectRatio,
        iconShape = MaterialShapes.Clover4Leaf,
        iconForegroundColor = Color.White,
        iconBackgroundColor = Color.Gray,
        destination = Screens.PlaybackQueue
    )
}
