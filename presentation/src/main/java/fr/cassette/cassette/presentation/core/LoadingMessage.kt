package fr.cassette.cassette.presentation.core

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun LoadingMessage(
    modifier: Modifier = Modifier,
    @StringRes message: Int
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularWavyProgressIndicator()
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingMessagePreview(){
    CassetteTheme {
        LoadingMessage(
            message = R.string.album_list_loading_albums
        )
    }
}
