package fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cassette.shared.presentation.generated.resources.Res
import cassette.shared.presentation.generated.resources.on_boarding_welcome_get_started
import cassette.shared.presentation.generated.resources.on_boarding_welcome_subtitle
import cassette.shared.presentation.generated.resources.on_boarding_welcome_title
import fr.cassettelabs.cassette.presentation.core.PrimaryButton
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme
import fr.cassettelabs.cassette.presentation.onBoarding.onBoardingWelcome.core.AnimatedCassetteHero
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun OnBoardingWelcomeScreen(
    uiState: OnBoardingWelcomeUiState,
    onEvent: (OnBoardingWelcomeEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(OnBoardingWelcomeEvent.OnAppearing)
    }

    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                PrimaryButton(
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onEvent(OnBoardingWelcomeEvent.OnGetStartedClicked)
                    },
                    text = stringResource(Res.string.on_boarding_welcome_get_started),
                )
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AnimatedCassetteHero(modifier = Modifier.padding(top = 16.dp))
            }
            item {
                Text(
                    text = stringResource(Res.string.on_boarding_welcome_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
                )
            }
            item {
                Text(
                    text = stringResource(Res.string.on_boarding_welcome_subtitle),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                )
            }
        }
    }
}

@Composable
@Preview
private fun OnBoardingWelcomeScreenPreview() {
    CassetteTheme {
        OnBoardingWelcomeScreen(
            uiState = OnBoardingWelcomeUiState(),
            onEvent = {},
        )
    }
}
