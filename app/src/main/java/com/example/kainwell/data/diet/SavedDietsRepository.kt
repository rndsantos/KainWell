package com.example.kainwell.data.diet

import com.example.kainwell.Diet
import com.example.kainwell.Diets
import kotlinx.coroutines.flow.Flow

interface SavedDietsRepository {
    fun savedDietsFlow(): Flow<Diets>

    suspend fun addDiet(diet: Diet)
}