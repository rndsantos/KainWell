package com.example.kainwell.ui.home.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.DietsEntity
import com.example.kainwell.data.diet.SavedDietsRepository
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.domain.updateData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
                DietUiState.Success(
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

    private fun DietsEntity.toDietList(foodItems: Map<String, Food>): List<Diet> {
        return dietsList.map { diet ->
            var totalCalories = 0.0
            var totalCholesterol = 0.0
            var totalFat = 0.0
            var totalSodium = 0.0
            var totalCarbohydrates = 0.0
            var totalFiber = 0.0
            var totalProtein = 0.0
            var totalVitA = 0.0
            var totalVitC = 0.0
            var totalCalcium = 0.0
            var totalIron = 0.0
            var totalCost = 0.0

            val foodItemsForDiet = diet.namesList.zip(diet.servingsList) { name, serving ->
                val food = foodItems[name] ?: Food()
                val updatedFood = food.updateData(serving)

                totalCalories += updatedFood.calories.toDouble()
                totalCholesterol += updatedFood.cholesterol.toDouble()
                totalFat += updatedFood.fat.toDouble()
                totalSodium += updatedFood.sodium.toDouble()
                totalCarbohydrates += updatedFood.carbohydrates.toDouble()
                totalFiber += updatedFood.fiber.toDouble()
                totalProtein += updatedFood.protein.toDouble()
                totalVitA += updatedFood.vitA.toDouble()
                totalVitC += updatedFood.vitC.toDouble()
                totalCalcium += updatedFood.calcium.toDouble()
                totalIron += updatedFood.iron.toDouble()
                totalCost += updatedFood.price

                updatedFood
            }

            Diet(
                foodItems = foodItemsForDiet,
                totalCalories = totalCalories.toFloat(),
                totalCholesterol = totalCholesterol.toFloat(),
                totalFat = totalFat.toFloat(),
                totalSodium = totalSodium.toFloat(),
                totalCarbohydrates = totalCarbohydrates.toFloat(),
                totalFiber = totalFiber.toFloat(),
                totalProtein = totalProtein.toFloat(),
                totalVitA = totalVitA.toFloat(),
                totalVitC = totalVitC.toFloat(),
                totalCalcium = totalCalcium.toFloat(),
                totalIron = totalIron.toFloat(),
                totalCost = totalCost.toFloat()
            )
        }
    }
}