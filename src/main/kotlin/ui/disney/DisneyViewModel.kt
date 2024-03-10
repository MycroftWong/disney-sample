package ui.disney

import api.ApiService
import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wang.wang.disney.data.SetupdbQueries

class DisneyViewModel(private val apiService: ApiService, private val setupdbQueries: SetupdbQueries) {

    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private val _loading = MutableStateFlow(false)
    private val _favoriteCharacters = MutableStateFlow(emptyList<Long>())

    val uiState: StateFlow<UiState> = combine(
        _loading,
        setupdbQueries.selectAll().asFlow().map { it.executeAsList() },
        _favoriteCharacters
    ) { loading, characters, favoriteCharacters ->
        UiState(loading, characters
            .map { item ->
                DisneyCharacterItem(item, favoriteCharacters.contains(item.id))
            })
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(1000), UiState(false, emptyList())
    )

    init {
        loadCharacters()
    }

    fun refresh() {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val characters = apiService.loadCharacters(1)
                withContext(Dispatchers.IO) {
                    setupdbQueries.transaction {
                        characters.data.forEach { character ->
                            setupdbQueries.insert(
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