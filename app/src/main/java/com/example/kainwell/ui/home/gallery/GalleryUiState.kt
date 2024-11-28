package com.example.kainwell.ui.home.gallery

import com.example.kainwell.data.food.Food

sealed interface GalleryUiState {
    data object Loading : GalleryUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : GalleryUiState

    data class Ready(
        val foodItems: List<Food>,
    ) : GalleryUiState
}