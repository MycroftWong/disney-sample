package model

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo(
    val count: Int,
    val nextPage: String?,
    val previousPage: String?,
    val totalPages: Int
)