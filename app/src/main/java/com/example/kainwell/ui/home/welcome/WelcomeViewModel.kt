package com.example.kainwell.ui.home.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.NutrientEntity
import com.example.kainwell.data.nutrient.NutritionalIntakesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val nutritionalIntakesRepository: NutritionalIntakesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<WelcomeUiState>(WelcomeUiState.Ready)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!nutritionalIntakesRepository.isEmpty())
                _uiState.value = WelcomeUiState.Finished
        }
    }

    fun setNutritionalIntakes(minimumNutrient: NutrientEntity, maximumNutrient: NutrientEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            nutritionalIntakesRepository.setMinimumNutritionalIntake(
                minimumNutrient
            )

            nutritionalIntakesRepository.setMaximumNutritionalIntake(
                maximumNutrient
            )

            _uiState.value = WelcomeUiState.Finished
        }
    }
}