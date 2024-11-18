package com.example.kainwell.data.food

import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoodItems(): Flow<List<Food>>

    fun getCategorizedFoodItems(): Flow<Map<String, List<Food>>>

    suspend fun getFood(id: String, category: String): Food?
}