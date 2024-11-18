package com.example.kainwell.ui.home.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : HomeScreenUiState

    data class Ready(
        val foodItems: Map<String, List<Food>>,
    ) : HomeScreenUiState
}

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.getCategorizedFoodItems().map {
                HomeScreenUiState.Ready(it)
            }.catch { throwable ->
                HomeScreenUiState.Error(throwable.message ?: "Unknown error")
            }.collect {
                _uiState.value = it
            }
        }
    }
}