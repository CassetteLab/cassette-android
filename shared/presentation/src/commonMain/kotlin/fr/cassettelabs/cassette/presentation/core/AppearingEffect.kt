package fr.cassettelabs.cassette.presentation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun AppearingEffect(block: suspend CoroutineScope.() -> Unit) {
    val currentBlock by rememberUpdatedState(block)
    var hasAppeared by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!hasAppeared) {
            hasAppeared = true
            currentBlock()
        }
    }
}
