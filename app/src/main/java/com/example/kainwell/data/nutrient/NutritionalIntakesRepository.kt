package com.example.kainwell.data.nutrient

import com.example.kainwell.NutrientEntity
import com.example.kainwell.NutritionalIntakesEntity

interface NutritionalIntakesRepository {
    suspend fun getNutritionalIntakes(): NutritionalIntakesEntity

    suspend fun isEmpty(): Boolean

    suspend fun clearNutritionalIntakes()

    suspend fun setMinimumNutritionalIntake(nutrient: NutrientEntity)

    suspend fun setMaximumNutritionalIntake(nutrient: NutrientEntity)
}

