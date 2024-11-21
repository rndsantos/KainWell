package com.example.kainwell.ui.food_detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val food = mutableStateOf(Food())

    init {
        val foodId: String = checkNotNull(savedStateHandle["foodId"])
        val foodCategory: String = checkNotNull(savedStateHandle["foodCategory"])
        viewModelScope.launch(Dispatchers.IO) {
            food.value = foodRepository.getFood(foodId, foodCategory) ?: Food()
        }
    }
}