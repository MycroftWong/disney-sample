package com.example.conposesample.ui.navigation

sealed interface Screen {
    val routeName: String

    data object Disney : Screen {
        override val routeName: String = "disney"
    }

    data class DisneyDetail(val characterId: Int) : Screen {
        override val routeName: String = "disney/$characterId"
    }
}
