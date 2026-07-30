package fr.cassettelabs.cassette.presentation.core

import cassette.shared.presentation.generated.resources.album_detail_shuffle
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ShuffleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialShapes.PixelCircle.toShape(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(
            imageVector = Icons.Rounded.Shuffle,
            contentDescription = stringResource(Res.string.album_detail_shuffle),
        )
    }
}

@Composable
@Preview
private fun ShuffleButtonPreview() {
    CassetteTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ShuffleButton(onClick = {})
        }
    }
}
