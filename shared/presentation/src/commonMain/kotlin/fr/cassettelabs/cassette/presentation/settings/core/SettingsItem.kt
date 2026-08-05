package fr.cassettelabs.cassette.presentation.settings.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    icon: ImageVector,
    iconShape: Shape = RoundedCornerShape(24.dp),
    iconForegroundColor: Color,
    iconBackgroundColor: Color,
    onClick:  () -> Unit
) {
    Button(
        modifier = modifier,
        shape = RectangleShape,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBackgroundColor, iconShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconForegroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    lineHeight = 8.sp
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    lineHeight = 8.sp
                )
            }
        }

    }
}

@Composable
@PreviewLightDark
private fun SettingsItemPreview(){
    CassetteTheme {
        SettingsItem(
            onClick = { },
            title = "Application information",
            subTitle = "View all app's info",
            icon = Icons.Rounded.Info,
            iconShape = MaterialShapes.PixelCircle.toShape(),
            iconForegroundColor = Color.White,
            iconBackgroundColor = Color.Blue
        )
    }
}
