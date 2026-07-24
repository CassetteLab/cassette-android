package fr.cassettelabs.cassette.presentation.core

import cassette.shared.presentation.generated.resources.on_boarding_welcome_get_started
import cassette.shared.presentation.generated.resources.Res

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.cassettelabs.cassette.presentation.core.theme.CassetteTheme

@Composable
internal fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        enabled = isEnabled,
    ) {
        content()
    }
}

@Composable
internal fun PrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
) {
    PrimaryButton(
        modifier = modifier,
        onClick = onClick,
        isEnabled = isEnabled,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp),
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
@Preview
private fun PrimaryButtonPreview() {
    CassetteTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                text = stringResource(Res.string.on_boarding_welcome_get_started),
            )

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                text = stringResource(Res.string.on_boarding_welcome_get_started),
                isLoading = true,
            )

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                text = stringResource(Res.string.on_boarding_welcome_get_started),
                isEnabled = false,
            )
        }
    }
}
