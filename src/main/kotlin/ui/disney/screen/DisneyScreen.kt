package ui.disney.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import ui.disney.DisneyCharacterItem
import ui.disney.viewmodel.DisneyViewModel

class DisneyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        DisneyScreen(getScreenModel()) { characterId ->
            navigator.push(DisneyDetailScreen(characterId))
        }
    }

}

@Composable
fun DisneyScreen(
    viewModel: DisneyViewModel,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(modifier = modifier) {
        DisneyContent(Modifier.fillMaxSize(), uiState, { viewModel.refresh() }, navigateToDetail)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DisneyContent(
    modifier: Modifier = Modifier,
    uiState: DisneyViewModel.UiState,
    onRefresh: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    val refreshState = rememberPullRefreshState(uiState.loading, onRefresh = onRefresh)
    Box(modifier = modifier.pullRefresh(refreshState)) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(uiState.disneyCharacterItems.size, key = { uiState.disneyCharacterItems[it].character.id }) {
                DisneyItem(
                    item = uiState.disneyCharacterItems[it],
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    navigateToDetail(it)
                }
            }
            if (uiState.disneyCharacterItems.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize()) {
                        Text(
                            text = "No characters found",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(
            uiState.loading,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = Color(0xFF0099CC)
        )
    }
}

@Composable
private fun DisneyItem(item: DisneyCharacterItem, modifier: Modifier, onItemClick: (Long) -> Unit) {
    Button(onClick = {
        onItemClick(item.character.id)
    }, modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (item.character.imageUrl != null) {
            AsyncImage(
                model = item.character.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.LightGray),
                modifier = Modifier
                    .size(width = 80.dp, height = 60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(
            text = item.character.name,
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically)
        )
    }
}

