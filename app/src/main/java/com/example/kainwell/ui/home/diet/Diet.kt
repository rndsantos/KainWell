package com.example.kainwell.ui.home.diet

import com.example.kainwell.data.food.Food

data class Diet(
    val foodItems: List<Food>,
    val totalCalories: Float,
    val totalCholesterol: Float,
    val totalFat: Float,
    val totalSodium: Float,
    val totalCarbohydrates: Float,
    val totalFiber: Float,
    val totalProtein: Float,
    val totalVitA: Float,
    val totalVitC: Float,
    val totalCalcium: Float,
    val totalIron: Float,
    val totalCost: Float,
)