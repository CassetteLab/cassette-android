package fr.cassette.cassette.presentation.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.cassette.cassette.presentation.R
import fr.cassette.cassette.presentation.core.navigation.Screens
import fr.cassette.cassette.presentation.core.theme.CassetteTheme
import fr.cassette.cassette.presentation.home.HomeEvent
import fr.cassette.cassette.presentation.home.HomeScreen
import fr.cassette.cassette.presentation.home.HomeViewModel
import fr.cassette.cassette.presentation.main.core.MainTab
import fr.cassette.cassette.presentation.settings.SettingsEvent
import fr.cassette.cassette.presentation.settings.SettingsScreen
import fr.cassette.cassette.presentation.settings.SettingsUiState
import fr.cassette.cassette.presentation.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MainScreen(
    onNavigateToRootScreen: (Screens) -> Unit
){
    val startDestination = MainTab.Home
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        bottomBar = {
            BottomAppBar(
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MainTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = tab == selectedDestination,
                            onClick = {
                                if (selectedDestination == tab) return@NavigationBarItem

                                selectedDestination = tab
                                navController.navigate(tab.destination){
                                    launchSingleTop = true
                                    popUpTo(navController.graph.id)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.iconRes,
                                    contentDescription = stringResource(tab.labelRes)
                                )
                            },
                            label = {
                                Text(text = stringResource(tab.labelRes))
                            },
                        )
                    }
                }
            }
        },
    ) { contentPadding ->

        NavHost(
            modifier = Modifier.padding(contentPadding),
            startDestination = startDestination.destination,
            navController = navController
        ) {
            composable<Screens.Home> {
                val viewModel: HomeViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when (event) {
                            is HomeEvent.OnAlbumClicked -> onNavigateToRootScreen(
                                Screens.AlbumDetail(albumId = event.albumId),
                            )

                            else -> Unit
                        }
                        viewModel.onEvent(event)
                    },
                )
            }

            composable<Screens.Settings> {
                val viewModel : SettingsViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SettingsScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        when(event){
                            SettingsEvent.OnServerConfigurationClicked -> { onNavigateToRootScreen(
                                Screens.SettingsServerConfiguration)
                            }
                            else -> Unit
                        }

                        viewModel.onEvent(event)
                    }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun MainScreenPreview() {
    CassetteTheme {
        MainScreen(
            onNavigateToRootScreen = { }
        )
    }
}
