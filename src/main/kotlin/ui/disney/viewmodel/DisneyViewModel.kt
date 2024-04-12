package ui.disney.viewmodel

import api.ApiService
import app.cash.sqldelight.coroutines.asFlow
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ui.base.ViewModel
import ui.disney.DisneyCharacterItem
import wang.wang.disney.data.DisneyCharacterQueries
import wang.wang.disney.data.FavoriteCharacterQueries

class DisneyViewModel(
    private val apiService: ApiService,
    private val disneyCharacterQueries: DisneyCharacterQueries,
    private val favoriteCharacterQueries: FavoriteCharacterQueries
) : ViewModel() {

    private val _loading = MutableStateFlow(false)

    val uiState: StateFlow<UiState> = combine(
        _loading,
        disneyCharacterQueries.selectAll().asFlow().map { it.executeAsList() },
        favoriteCharacterQueries.selectAll().asFlow().map { it.executeAsList() }
    ) { loading, characters, favoriteCharacters ->
        val favoriteIds = favoriteCharacters.filter { it.favorite == 1L }.map { it.characterId }
        UiState(loading, characters
            .map { item ->
                DisneyCharacterItem(item, favoriteIds.contains(item.id))
            })
    }.stateIn(
        screenModelScope, SharingStarted.WhileSubscribed(1000), UiState(false, emptyList())
    )

    init {
        loadCharacters()
    }

    fun refresh() {
        loadCharacters()
    }

    private fun loadCharacters() {
        screenModelScope.launch {
            _loading.value = true
            try {
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
            } finally {
                _loading.value = false
            }
        }
    }

    data class UiState(
        val loading: Boolean,
        val disneyCharacterItems: List<DisneyCharacterItem>,
    )
}