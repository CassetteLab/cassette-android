package fr.cassettelabs.cassette

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.awaitApplication
import fr.cassettelabs.cassette.di.sharedModules
import fr.cassettelabs.cassette.domain.usecases.configuration.HasServerConfigurationUseCase
import java.awt.Taskbar
import javax.imageio.ImageIO
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

fun main() = runBlocking {
    configureDesktopIcon()

    startKoin {
        modules(sharedModules())
    }

    awaitApplication {

        val hasServerConfigurationUseCase : HasServerConfigurationUseCase = get(HasServerConfigurationUseCase::class.java)
        var startupConfiguration by remember { mutableStateOf<Boolean?>(null) }

        LaunchedEffect(Unit) {
            startupConfiguration = hasServerConfigurationUseCase()
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "Cassette",
            icon = painterResource(desktopIconResourcePath()),
        ) {
            val hasValidServerConfiguration = startupConfiguration
            if (hasValidServerConfiguration == null) {
                DesktopSplashScreen()
            } else {
                App(hasValidServerConfiguration = hasValidServerConfiguration)
            }
        }
    }
}

private fun configureDesktopIcon() {
    if (!Taskbar.isTaskbarSupported() || !Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
        return
    }

    val icon = Thread.currentThread().contextClassLoader.getResource(desktopIconResourcePath()) ?: return
    Taskbar.getTaskbar().iconImage = ImageIO.read(icon)
}

private fun desktopIconResourcePath(): String =
    if (System.getProperty("os.name").contains("mac", ignoreCase = true)) {
        "icons/cassette-icon-macos.png"
    } else {
        "icons/cassette-icon.png"
    }

@Composable
private fun DesktopSplashScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0D1A)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource("icons/cassette-icon.svg"),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
