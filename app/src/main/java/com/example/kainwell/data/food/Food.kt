package com.example.kainwell.data.food

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.firestore.DocumentId

@IgnoreExtraProperties
data class Food(
    @DocumentId val id: String = "",
    val name: String = "",
    val serving: Float = 1f,
    val price: Float = 0f,
    val servingSize: String = "",
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
    val category: String = "",
    val img: String = "",
) {
    fun nutrientsToList(): List<Float> {
        return listOf(
            calories,
            cholesterol,
            fat,
            sodium,
            carbohydrates,
            fiber,
            protein,
            vitA,
            vitC,
            calcium,
            iron
        )
    }
}