package com.example.kainwell.data.diet

import com.example.kainwell.DietEntity
import com.example.kainwell.DietsEntity
import kotlinx.coroutines.flow.Flow

interface SavedDietsRepository {
    fun savedDietsFlow(): Flow<DietsEntity>

    suspend fun addDiet(diet: DietEntity)

    suspend fun clearSavedDiets()
}