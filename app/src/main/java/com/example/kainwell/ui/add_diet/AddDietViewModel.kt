package com.example.kainwell.ui.add_diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.Nutrient
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.data.nutrient.NutritionalIntakeRepository
import com.example.kainwell.domain.OptimizeSelectedFoodItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddDietViewModel @Inject constructor(
    foodRepository: FoodRepository,
    nutritionalIntakeRepository: NutritionalIntakeRepository,
    private val optimizeSelectedFoodItemsUseCase: OptimizeSelectedFoodItemsUseCase,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _selectedFoodItems = MutableStateFlow(emptySet<Food>())

    private val _uiState = MutableStateFlow<AddDietUiState>(AddDietUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            nutritionalIntakeRepository.setMinimumNutritionalIntake(
                Nutrient.newBuilder().apply {
                    calories = 2000f
                    cholesterol = 0f
                    fat = 0f
                    sodium = 0f
                    carbohydrates = 0f
                    fiber = 25f
                    protein = 50f
                    vitA = 5000f
                    vitC = 50f
                    calcium = 800f
                    iron = 10f
                }.build()
            )

            nutritionalIntakeRepository.setMaximumNutritionalIntake(
                Nutrient.newBuilder().apply {
                    calories = 2250f
                    cholesterol = 300f
                    fat = 65f
                    sodium = 2400f
                    carbohydrates = 300f
                    fiber = 100f
                    protein = 100f
                    vitA = 50000f
                    vitC = 20000f
                    calcium = 1600f
                    iron = 30f
                }.build()
            )

            combine(
                foodRepository.getCategorizedFoodItems(),
                _query,
                _selectedFoodItems
            ) { foodItems, query, selectedFoodItems ->
                AddDietUiState.Ready(
                    foodItems = foodItems.filter {
                        it.key.contains(query, ignoreCase = true)
                    },
                    selectedFoodItems = selectedFoodItems
                )
            }.catch { throwable ->
                AddDietUiState.Error(throwable.message ?: "Unknown error")
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onAddSelectedFoodItem(food: Food) {
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableSet().apply {
            add(food)
        }
    }

    fun onRemoveSelectedFoodItem(food: Food) {
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableSet().apply {
            remove(food)
        }
    }

    fun onResetSelectedFoodItems() {
        _selectedFoodItems.value = emptySet()
    }

    fun onOptimizeMeal() {
        viewModelScope.launch(Dispatchers.IO) {
            optimizeSelectedFoodItemsUseCase(_selectedFoodItems.value.toList())
        }
    }
}
