import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import di.CoreModule
import di.ViewModelModule
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import ui.disney.screen.DisneyScreen

@Preview
@Composable
fun App() {
    val koinApplication = KoinApplication.init().apply {
        modules(CoreModule(), ViewModelModule())
    }
    KoinApplication(application = { koinApplication }) {
        MaterialTheme {
            Navigator(DisneyScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Disney") {
        App()
    }
}
