package ui.disney.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.*
import ui.base.ViewModel
import wang.wang.disney.data.DisneyCharacter
import wang.wang.disney.data.DisneyCharacterQueries
import wang.wang.disney.data.FavoriteCharacterQueries

class DisneyDetailViewModel(
    private val disneyCharacterQueries: DisneyCharacterQueries,
    private val favoriteCharacterQueries: FavoriteCharacterQueries,
    private val characterId: Long,
) : ViewModel() {

    val uiState: StateFlow<UiState> = combine(
        disneyCharacterQueries.selectById(characterId).asFlow().map { it.executeAsOneOrNull() },
        favoriteCharacterQueries.selectById(characterId).asFlow().map { it.executeAsOneOrNull() }
    ) { character, favoriteCharacter ->
        UiState(character, favoriteCharacter != null)
    }.stateIn(screenModelScope, SharingStarted.WhileSubscribed(5_000), UiState(character = null))

    data class UiState(
        val character: DisneyCharacter?,
        val favorite: Boolean = false,
    )
}