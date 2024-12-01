package com.example.kainwell.ui.welcome

sealed interface WelcomeUiState {
    data object Ready : WelcomeUiState
    data object Finished : WelcomeUiState
    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : WelcomeUiState

}