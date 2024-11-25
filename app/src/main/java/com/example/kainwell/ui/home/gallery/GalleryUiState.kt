package com.example.kainwell.ui.home.gallery

import com.example.kainwell.data.food.Food

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : HomeScreenUiState

    data class Ready(
        val foodItems: List<Food>,
    ) : HomeScreenUiState
}