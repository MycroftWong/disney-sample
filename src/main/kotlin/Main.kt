import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import di.coreModule
import org.koin.compose.KoinApplication
import ui.disney.DisneyScreen

@Preview
@Composable
fun App() {
    KoinApplication(application = {
        modules(coreModule())
    }) {
        MaterialTheme {
            DisneyScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Disney") {
        App()
    }
}
