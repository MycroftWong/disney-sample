package model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteCharacter(
    val characterId: Int,
    val favorite: Int,
    val createdAt: Instant = Clock.System.now(),
) {
}