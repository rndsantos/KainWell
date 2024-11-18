package com.example.kainwell.ui.add_diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class AddDietViewModel @Inject constructor(
    foodRepository: FoodRepository,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _selectedFoodItems = MutableStateFlow<Map<Food, Int>>(emptyMap())

    private val _uiState = MutableStateFlow<AddDietUiState>(AddDietUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
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
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableMap().apply {
            this[food] = this.getOrDefault(food, 0) + 1
        }
    }

    fun onRemoveSelectedFoodItem(food: Food) {
        _selectedFoodItems.value = _selectedFoodItems.value.toMutableMap().apply {
            if (this[food] == 1) {
                remove(food)
            } else {
                this[food] = this.getOrDefault(food, 0) - 1
            }
        }
    }

    fun onSelectedFoodItemsChange(selectedFoodItems: Map<Food, Int>) {
        _selectedFoodItems.value = selectedFoodItems
    }
}
