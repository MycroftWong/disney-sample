package ui.disney.viewmodel

import api.ApiService
import app.cash.sqldelight.coroutines.asFlow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import ui.disney.DisneyCharacterItem
import wang.wang.disney.data.DisneyCharacterQueries
import wang.wang.disney.data.FavoriteCharacterQueries

class DisneyViewModel(
    private val apiService: ApiService,
    private val disneyCharacterQueries: DisneyCharacterQueries,
    private val favoriteCharacterQueries: FavoriteCharacterQueries
) : ScreenModel {

    private val _loading = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        _loading,
        disneyCharacterQueries.selectAll().asFlow().map { it.executeAsList() },
        favoriteCharacterQueries.selectAll().asFlow().map { it.executeAsList() }
    ) { loading, characters, favoriteCharacters ->
        val favoriteIds = favoriteCharacters.filter { it.favorite == 1L }.map { it.characterId }
        UiState.Data(characters
            .map { item ->
                DisneyCharacterItem(item, favoriteIds.contains(item.id))
            })
    }.stateIn(
        screenModelScope, SharingStarted.WhileSubscribed(1000), UiState.Loading
    )

    init {
        loadCharacters()
    }

    fun refresh() {
        loadCharacters()
    }

    private fun loadCharacters() {
        screenModelScope.launch {
            val characters = apiService.loadCharacters(1)
            withContext(Dispatchers.IO) {
                disneyCharacterQueries.transaction {
                    characters.data.forEach { character ->
                        disneyCharacterQueries.insert(
                            character.id,
                            character.createdAt,
                            character.imageUrl,
                            character.name,
                            character.url
                        )
                    }
                }
            }
        }
    }

    fun toggleFavorite(characterId: Long) {
        screenModelScope.launch {
            _loading.value = true
            try {
                withContext(Dispatchers.IO) {
                    favoriteCharacterQueries.transaction {
                        val favorite = favoriteCharacterQueries.selectById(characterId).executeAsOneOrNull()
                        if (favorite == null) {
                            favoriteCharacterQueries.insert(characterId, 1, Clock.System.now().toString())
                        } else {
                            favoriteCharacterQueries.delete(characterId)
                        }
                    }
                }
            } finally {
                _loading.value = false
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Data(val disneyCharacterItems: List<DisneyCharacterItem>) : UiState
    }
}