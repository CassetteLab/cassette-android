package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingCache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun OnBoardingCacheScreen(
    uiState: OnBoardingCacheUiState,
    onEvent: (OnBoardingCacheEvent) -> Unit,
) {
    Scaffold { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
        }
    }
}

@Composable
@Preview
private fun OnBoardingCacheScreenPreview() {
    CassetteTheme {
        OnBoardingCacheScreen(
            uiState = OnBoardingCacheUiState(),
            onEvent = {},
        )
    }
}
