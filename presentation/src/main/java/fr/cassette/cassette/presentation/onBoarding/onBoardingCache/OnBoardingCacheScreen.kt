package fr.cassette.cassette.presentation.onBoarding.onBoardingCache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun OnBoardingCacheScreen(
    uiState: OnBoardingCacheUiState,
    onEvent: (OnBoardingCacheEvent) -> Unit,
) {
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
        ) {
        }
    }
}

@Composable
@Preview
private fun OnBoardingCacheScreenPreview() {
    OnBoardingCacheScreen(
        uiState = OnBoardingCacheUiState(),
        onEvent = {},
    )
}
