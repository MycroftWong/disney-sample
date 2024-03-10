package ui.disney

import wang.wang.disney.data.DisneyCharacter


data class DisneyCharacterItem(
    val character: DisneyCharacter,
    val favorite: Boolean = false,
)