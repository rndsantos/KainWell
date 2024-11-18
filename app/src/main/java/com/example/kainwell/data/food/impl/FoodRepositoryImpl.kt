package com.example.kainwell.data.food.impl

import com.example.kainwell.data.food.Food
import com.example.kainwell.data.food.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FoodRepository {
    private inline fun <T1, T2, T3, T4, T5, T6, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6
        ) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
            )
        }
    }

    override fun getAllFoodItems(): Flow<List<Food>> {
        return firestore.collectionGroup(FOOD_GROUP).dataObjects()
    }

    override fun getCategorizedFoodItems(): Flow<Map<String, List<Food>>> {
        return combine(
            firestore.collection(FOOD_COLLECTION).document(CARBS).collection(FOOD_GROUP)
                .dataObjects(),
            firestore.collection(FOOD_COLLECTION).document(DAIRY).collection(FOOD_GROUP)
                .dataObjects(),
            firestore.collection(FOOD_COLLECTION).document(FATS).collection(FOOD_GROUP)
                .dataObjects(),
            firestore.collection(FOOD_COLLECTION).document(FRUITS).collection(FOOD_GROUP)
                .dataObjects(),
            firestore.collection(FOOD_COLLECTION).document(PROTEINS).collection(FOOD_GROUP)
                .dataObjects(),
            firestore.collection(FOOD_COLLECTION).document(VEGETABLES).collection(FOOD_GROUP)
                .dataObjects()
        ) {
                carbs: List<Food>?,
                dairy: List<Food>?,
                fats: List<Food>?,
                fruits: List<Food>?,
                protein: List<Food>?,
                vegetables: List<Food>?,
            ->
            mapOf(
                CARBS to carbs as List<Food>,
                DAIRY to dairy as List<Food>,
                FATS to fats as List<Food>,
                FRUITS to fruits as List<Food>,
                PROTEINS to protein as List<Food>,
                VEGETABLES to vegetables as List<Food>,
            )
        }
    }

    override suspend fun getFood(id: String, category: String): Food? =
        firestore.collection(FOOD_COLLECTION).document(category).collection(FOOD_GROUP).document(id)
            .get().await().toObject<Food>()

    companion object {
        private const val FOOD_COLLECTION = "foods"
        private const val FOOD_GROUP = "items"
        private const val CARBS = "carbs"
        private const val DAIRY = "dairy"
        private const val FATS = "fats"
        private const val FRUITS = "fruits"
        private const val PROTEINS = "proteins"
        private const val VEGETABLES = "vegetables"
    }
}

