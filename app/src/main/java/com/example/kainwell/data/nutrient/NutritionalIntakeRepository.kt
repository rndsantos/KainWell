package com.example.kainwell.data.nutrient

import com.example.kainwell.Nutrient
import com.example.kainwell.NutritionalIntakes

interface NutritionalIntakeRepository {
    suspend fun getNutritionalIntakes(): NutritionalIntakes

    suspend fun setMinimumNutritionalIntake(nutrient: Nutrient)

    suspend fun setMaximumNutritionalIntake(nutrient: Nutrient)
}

