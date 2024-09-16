package ui.disney.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import ui.disney.DisneyCharacterItem
import ui.disney.viewmodel.DisneyViewModel

class DisneyScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        DisneyScreen(koinScreenModel()) { characterId ->
            navigator.push(DisneyDetailScreen(characterId))
        }
    }

}

@Composable
internal fun DisneyScreen(
    viewModel: DisneyViewModel,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    Box(modifier = modifier) {
        DisneyContent(Modifier.fillMaxSize(), state, { viewModel.refresh() }, navigateToDetail) { characterId ->
            viewModel.toggleFavorite(characterId)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DisneyContent(
    modifier: Modifier = Modifier,
    state: DisneyViewModel.UiState,
    onRefresh: () -> Unit,
    navigateToDetail: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit
) {
    val loadingCharacters = (state == DisneyViewModel.UiState.Loading)
    val refreshState = rememberPullRefreshState(loadingCharacters, onRefresh = onRefresh)

    Box(modifier = modifier.pullRefresh(refreshState)) {
        LazyColumn(Modifier.fillMaxSize()) {
            when (state) {
                DisneyViewModel.UiState.Loading -> {
                    item {
                        Box(Modifier.fillParentMaxSize()) {
                            Text(
                                text = "Loading characters...",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                is DisneyViewModel.UiState.Data -> {
                    items(
                        state.disneyCharacterItems.size,
                        key = { state.disneyCharacterItems[it].character.id }) { index ->
                        DisneyItem(
                            item = state.disneyCharacterItems[index],
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            onItemClick = {
                                navigateToDetail(it)
                            },
                            onFavoriteClick = { characterId ->
                                onFavoriteClick(characterId)
                            }
                        )
                    }
                    if (state.disneyCharacterItems.isEmpty()) {
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
            }
        }
        PullRefreshIndicator(
            loadingCharacters,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = Color(0xFF0099CC)
        )
    }
}

@Composable
private fun DisneyItem(
    item: DisneyCharacterItem,
    modifier: Modifier,
    onItemClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit
) {
    Row(modifier
        .clickable { onItemClick(item.character.id) }
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
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
        Switch(
            checked = item.favorite,
            onCheckedChange = { onFavoriteClick(item.character.id) },
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

