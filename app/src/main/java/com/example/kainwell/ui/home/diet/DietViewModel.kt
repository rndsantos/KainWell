package com.example.kainwell.ui.home.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.DietsEntity
import com.example.kainwell.data.diet.SavedDietsRepository
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.domain.round
import com.example.kainwell.domain.updateData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DietViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val savedDietsRepository: SavedDietsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DietUiState>(DietUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                foodRepository.getAllFoodItems(),
                savedDietsRepository.savedDietsFlow(),
            ) { foodItems, savedDiets ->
                DietUiState.Ready(
                    savedDiets = savedDiets.toDietList(foodItems.associateBy { it.name })
                )
            }.catch { throwable ->
                DietUiState.Error(
                    errorMessage = throwable.message ?: "Unknown error",
                    errorType = throwable
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun onBack() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = DietUiState.Ready(
                savedDiets = savedDietsRepository.savedDietsFlow().first()
                    .toDietList(foodRepository.getAllFoodItems().first().associateBy { it.name })
            )
        }
    }

    private fun DietsEntity.toDietList(foodItems: Map<String, Food>): List<Diet> {
        return dietsList.map { diet ->
            var totalCalories = 0f
            var totalCholesterol = 0f
            var totalFat = 0f
            var totalSodium = 0f
            var totalCarbohydrates = 0f
            var totalFiber = 0f
            var totalProtein = 0f
            var totalVitA = 0f
            var totalVitC = 0f
            var totalCalcium = 0f
            var totalIron = 0f
            var totalCost = 0f

            val foodItemsForDiet = diet.namesList.zip(diet.servingsList) { name, serving ->
                val food = foodItems[name] ?: Food()
                val updatedFood = food.updateData(serving)

                totalCalories += updatedFood.calories
                totalCholesterol += updatedFood.cholesterol
                totalFat += updatedFood.fat
                totalSodium += updatedFood.sodium
                totalCarbohydrates += updatedFood.carbohydrates
                totalFiber += updatedFood.fiber
                totalProtein += updatedFood.protein
                totalVitA += updatedFood.vitA
                totalVitC += updatedFood.vitC
                totalCalcium += updatedFood.calcium
                totalIron += updatedFood.iron
                totalCost += updatedFood.price

                updatedFood
            }

            Diet(
                foodItems = foodItemsForDiet,
                totalCalories = totalCalories.round(),
                totalCholesterol = totalCholesterol.round(),
                totalFat = totalFat.round(),
                totalSodium = totalSodium.round(),
                totalCarbohydrates = totalCarbohydrates.round(),
                totalFiber = totalFiber.round(),
                totalProtein = totalProtein.round(),
                totalVitA = totalVitA.round(),
                totalVitC = totalVitC.round(),
                totalCalcium = totalCalcium.round(),
                totalIron = totalIron.round(),
                totalCost = totalCost.round()
            )
        }
    }
}