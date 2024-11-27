package com.example.kainwell.ui.add_diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.DietEntity
import com.example.kainwell.NutrientEntity
import com.example.kainwell.data.diet.SavedDietsRepository
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.data.nutrient.NutritionalIntakesRepository
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
    nutritionalIntakesRepository: NutritionalIntakesRepository,
    private val savedDietsRepository: SavedDietsRepository,
    private val optimizeSelectedFoodItemsUseCase: OptimizeSelectedFoodItemsUseCase,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _selectedFoodItems = MutableStateFlow(emptyList<Food>())
    private val _optimizedDiet = MutableStateFlow(emptyList<Food>())

    private val _uiState = MutableStateFlow<AddDietUiState>(AddDietUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            nutritionalIntakesRepository.setMinimumNutritionalIntake(
                NutrientEntity.newBuilder().apply {
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

            nutritionalIntakesRepository.setMaximumNutritionalIntake(
                NutrientEntity.newBuilder().apply {
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
                _selectedFoodItems,
                _optimizedDiet
            ) { foodItems, query, selectedFoodItems, optimizedDiet ->
                AddDietUiState.Ready(
                    foodItems = foodItems.filter(query),
                    selectedFoodItems = selectedFoodItems,
                    optimizedDiet = optimizedDiet
                )
            }.catch { throwable ->
                AddDietUiState.Error(throwable.message ?: "Unknown error")
            }.collect {
                _uiState.value = it
            }
        }
    }

    private fun Map<String, List<Food>>.filter(query: String): Map<String, List<Food>> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return this

        return this.mapValues { (_, foods) ->
            foods.filter { food ->
                food.name.contains(trimmedQuery, ignoreCase = true)
            }
        }.filter { foods ->
            foods.value.isNotEmpty()
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onAddSelectedFoodItem(food: Food) {
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableList().apply {
            add(food)
        }
    }

    fun onRemoveSelectedFoodItem(food: Food) {
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableList().apply {
            remove(food)
        }
    }

    fun onSelectAllFoodItems(foodItems: List<Food>) {
        _selectedFoodItems.value = foodItems
    }

    fun onResetSelectedFoodItems() {
        _selectedFoodItems.value = emptyList()
    }

    fun onAddDiet() {
        viewModelScope.launch(Dispatchers.IO) {
            savedDietsRepository.addDiet(
                DietEntity.newBuilder()
                    .addAllNames(_optimizedDiet.value.map { it.name })
                    .addAllServings(_optimizedDiet.value.map { it.serving })
                    .build()
            )
        }
    }

    fun onOptimizeMeal() {
        _optimizedDiet.value = emptyList()
        _uiState.value = AddDietUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _optimizedDiet.value =
                optimizeSelectedFoodItemsUseCase(_selectedFoodItems.value)
        }
    }
}
