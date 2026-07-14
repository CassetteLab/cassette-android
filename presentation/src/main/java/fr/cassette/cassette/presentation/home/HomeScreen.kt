package fr.cassette.cassette.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun HomeScreen() {
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Text("HomeScreen")
        }
    }
}