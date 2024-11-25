package com.example.kainwell.data.nutrient

import com.example.kainwell.NutrientEntity
import com.example.kainwell.NutritionalIntakesEntity

interface NutritionalIntakesRepository {
    suspend fun getNutritionalIntakes(): NutritionalIntakesEntity

    suspend fun setMinimumNutritionalIntake(nutrient: NutrientEntity)

    suspend fun setMaximumNutritionalIntake(nutrient: NutrientEntity)
}

