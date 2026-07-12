package fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.onBoarding.onBoardingWelcome.core.AnimatedCassetteHero
import fr.cassette.cassette.presentation.ui.theme.CassetteTheme

@Composable
internal fun OnBoardingWelcomeScreen(
    uiState: OnBoardingWelcomeUiState,
    onEvent: (OnBoardingWelcomeEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        onEvent(OnBoardingWelcomeEvent.OnAppearing)
    }

    val heroAlpha by animateFloatAsState(
        targetValue = if (uiState.hasAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (uiState.hasAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 80),
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (uiState.hasAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 180),
    )
    val hapticFeedback = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            AnimatedCassetteHero(
                modifier = Modifier
                    .alpha(heroAlpha)
                    .scale(0.7f + heroAlpha * 0.3f),
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier
                    .alpha(contentAlpha)
                    .offset(y = ((1f - contentAlpha) * 24).dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.on_boarding_welcome_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
                )
                Text(
                    text = stringResource(R.string.on_boarding_welcome_subtitle),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEvent(OnBoardingWelcomeEvent.OnGetStartedClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .alpha(buttonAlpha)
                    .offset(y = ((1f - buttonAlpha) * 30).dp),
                enabled = uiState.isGetStartedEnabled,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.on_boarding_welcome_get_started),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
@Preview
internal fun OnBoardingWelcomeScreenPreview() {
    CassetteTheme {
        OnBoardingWelcomeScreen(
            uiState = OnBoardingWelcomeUiState(hasAppeared = true),
            onEvent = {},
        )
    }
}
