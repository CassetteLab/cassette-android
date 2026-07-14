package fr.cassette.cassette.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.cassette.cassette.presentation.navigation.CassetteNavigation
import fr.cassette.cassette.presentation.ui.theme.CassetteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CassetteTheme {
                CassetteNavigation()
            }
        }
    }
}
