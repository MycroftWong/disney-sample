package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisneyCharacter(
    @SerialName("_id")
    val id: Long,
    val createdAt: String,
    val name: String,
    val url: String?,
    val imageUrl: String? = null,
)