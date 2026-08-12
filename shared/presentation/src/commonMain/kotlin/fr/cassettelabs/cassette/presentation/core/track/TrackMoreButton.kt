package fr.cassettelabs.cassette.presentation.core.track

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_detail_menu
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TrackMoreButton(
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        modifier =
            Modifier
                .size(36.dp)
                .padding(end = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = stringResource(Res.string.playlist_detail_menu),
        )
    }
}

@Composable
@PreviewLightDark
private fun TrackMoreButtonPreview(){
    CassetteTheme {
        TrackMoreButton(
            onClick = {}
        )
    }
}
