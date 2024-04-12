import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import di.CoreModule
import di.ViewModelModule
import org.koin.compose.KoinApplication
import ui.disney.screen.DisneyScreen

@Preview
@Composable
fun App() {
    KoinApplication(application = {
        modules(CoreModule(), ViewModelModule())
    }) {
        MaterialTheme {
            Navigator(DisneyScreen())
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Disney") {
        App()
    }
}
