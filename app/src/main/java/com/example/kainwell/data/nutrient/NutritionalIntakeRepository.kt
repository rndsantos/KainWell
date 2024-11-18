package com.example.kainwell.data.nutrient

import kotlinx.coroutines.flow.Flow

interface NutritionalIntakeRepository {
    fun getNutritionalIntake(): Flow<Nutrient>
}