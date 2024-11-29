package com.example.kainwell.ui.home.diet

sealed interface DietUiState {
    data object Loading : DietUiState
    data class Ready(val savedDiets: List<Diet>) : DietUiState
    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : DietUiState
}

