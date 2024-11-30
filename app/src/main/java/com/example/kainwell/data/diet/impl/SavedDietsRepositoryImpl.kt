package com.example.kainwell.data.diet.impl

import androidx.datastore.core.DataStore
import com.example.kainwell.DietEntity
import com.example.kainwell.DietsEntity
import com.example.kainwell.data.diet.SavedDietsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavedDietsRepositoryImpl @Inject constructor(
    private val savedDietsDataStore: DataStore<DietsEntity>,
) : SavedDietsRepository {
    override fun savedDietsFlow(): Flow<DietsEntity> =
        savedDietsDataStore.data.map {
            it
        }

    override suspend fun addDiet(diet: DietEntity) {
        savedDietsDataStore.updateData { currentDiets ->
            currentDiets.toBuilder()
                .addDiets(diet)
                .build()
        }
    }

    override suspend fun updateDietTitle(diet: DietEntity, title: String) {
        savedDietsDataStore.updateData { currentDiets ->
            currentDiets.toBuilder().setDiets(
                currentDiets.dietsList.indexOfFirst { it.title == diet.title },
                diet.toBuilder().setTitle(title).build()
            ).build()
        }
    }

    override suspend fun clearSavedDiets() {
        savedDietsDataStore.updateData {
            DietsEntity.getDefaultInstance()
        }
    }

    override suspend fun savedDietsCount(): Int = savedDietsDataStore.data.map {
        it.dietsCount
    }.first()
}