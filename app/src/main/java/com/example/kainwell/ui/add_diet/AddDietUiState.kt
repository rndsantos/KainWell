package com.example.kainwell.ui.add_diet

import com.example.kainwell.data.food.Food

sealed interface AddDietUiState {
    data object Loading : AddDietUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : AddDietUiState

    data class Ready(
        val foodItems: Map<String, List<Food>>,
        val selectedFoodItems: Map<Food, Int> = mapOf(),
    ) : AddDietUiState
}