package com.example.kainwell.data.nutrient

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Nutrient(
    val calories: Float = 0f,
    val cholesterol: Float = 0f,
    val fat: Float = 0f,
    val sodium: Float = 0f,
    val carbohydrates: Float = 0f,
    val fiber: Float = 0f,
    val protein: Float = 0f,
    val vitA: Float = 0f,
    val vitC: Float = 0f,
    val calcium: Float = 0f,
    val iron: Float = 0f,
)