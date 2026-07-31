package fr.cassettelabs.cassette.presentation.core

import cassette.shared.presentation.generated.resources.album_list_loading_albums
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LoadingMessage(
    modifier: Modifier = Modifier,
    message: StringResource,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularWavyProgressIndicator()
        Text(
            text = stringResource(message),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun LoadingMessagePreview(){
    CassetteTheme {
        LoadingMessage(
            message = Res.string.album_list_loading_albums
        )
    }
}
