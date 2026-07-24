package fr.cassettelabs.cassette.presentation.albumDetail.core

import cassette.shared.presentation.generated.resources.album_detail_back
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp

@Composable
internal fun AlbumDetailBackButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.86f),
            ),
        modifier =
            Modifier
                .statusBarsPadding()
                .padding(start = 12.dp, top = 4.dp),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.album_detail_back),
        )
    }
}
