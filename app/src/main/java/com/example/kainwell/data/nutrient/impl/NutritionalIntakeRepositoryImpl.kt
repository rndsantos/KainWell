package com.example.kainwell.data.nutrient.impl

import androidx.datastore.core.DataStore
import com.example.kainwell.data.nutrient.Nutrient
import com.example.kainwell.data.nutrient.NutritionalIntakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NutritionalIntakeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Nutrient>,
) : NutritionalIntakeRepository {
    override fun getNutritionalIntake(): Flow<Nutrient> {
        return dataStore.data
    }
}
