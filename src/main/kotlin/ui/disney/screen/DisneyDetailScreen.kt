package ui.disney.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.koin.core.parameter.parametersOf
import ui.disney.viewmodel.DisneyDetailViewModel

class DisneyDetailScreen(private val characterId: Long) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val disneyDetailViewModel = getScreenModel<DisneyDetailViewModel>(parameters = {
            parametersOf(characterId)
        })
        DisneyDetailScreen(disneyDetailViewModel) {
            navigator.pop()
        }
    }
}

@Composable
fun DisneyDetailScreen(
    viewModel: DisneyDetailViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(modifier = modifier) {
        DisneyDetailContent(Modifier.fillMaxSize(), uiState, onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisneyDetailContent(
    modifier: Modifier = Modifier,
    uiState: DisneyDetailViewModel.UiState,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Disney") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }) {
        if (uiState.character != null) {
            Column(modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                AsyncImage(
                    model = uiState.character.imageUrl,
                    contentDescription = uiState.character.name,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.character.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = "Character not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

    }

}