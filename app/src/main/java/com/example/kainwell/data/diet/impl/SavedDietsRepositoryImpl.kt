package com.example.kainwell.data.diet.impl

import androidx.datastore.core.DataStore
import com.example.kainwell.Diet
import com.example.kainwell.Diets
import com.example.kainwell.data.diet.SavedDietsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavedDietsRepositoryImpl @Inject constructor(
    private val savedDietsDataStore: DataStore<Diets>,
) : SavedDietsRepository {
    override fun savedDietsFlow(): Flow<Diets> =
        savedDietsDataStore.data.map {
            it
        }

    override suspend fun addDiet(diet: Diet) {
        savedDietsDataStore.updateData { currentDiets ->
            currentDiets.toBuilder()
                .addDiets(diet)
                .build()
        }
    }
}