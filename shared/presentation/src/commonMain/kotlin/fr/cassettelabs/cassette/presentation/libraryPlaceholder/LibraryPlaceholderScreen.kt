package fr.cassettelabs.cassette.presentation.libraryPlaceholder

import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.home_artists_title
import cassette.shared.presentation.generated.resources.library_placeholder_back
import cassette.shared.presentation.generated.resources.library_placeholder_description
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryPlaceholderScreen(
    title: StringResource,
    contentPadding: PaddingValues = PaddingValues(),
    onEvent: (LibraryPlaceholderEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                navigationIcon = {
                    IconButton(onClick = { onEvent(LibraryPlaceholderEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.library_placeholder_back),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding.plus(contentPadding))
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.library_placeholder_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun LibraryPlaceholderScreenPreview() {
    CassetteTheme {
        LibraryPlaceholderScreen(
            title = Res.string.home_artists_title,
            onEvent = {},
        )
    }
}
