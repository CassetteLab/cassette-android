package fr.cassettelabs.cassette.presentation.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.playlist_create_back
import cassette.shared.presentation.generated.resources.playlist_create_title
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CassetteTopAppBar(
    title: StringResource,
    onBackClicked: (() -> Unit)? = null
) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        navigationIcon = if (onBackClicked == null) { {} } else {
            {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.playlist_create_back),
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.headlineLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
    )
}

@Composable
@PreviewLightDark
private fun CassetteTopAppBarPreview(){
    CassetteTheme {
        CassetteTopAppBar(
            title = Res.string.playlist_create_title
        )
    }
}
