package com.example.kainwell.data.nutrient.impl

import androidx.datastore.core.DataStore
import com.example.kainwell.NutrientEntity
import com.example.kainwell.NutritionalIntakesEntity
import com.example.kainwell.data.nutrient.NutritionalIntakesRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import javax.inject.Inject

class NutritionalIntakesRepositoryImpl @Inject constructor(
    private val nutritionalIntakeDataStore: DataStore<NutritionalIntakesEntity>,
) : NutritionalIntakesRepository {
    override suspend fun getNutritionalIntakes(): NutritionalIntakesEntity =
        nutritionalIntakeDataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(NutritionalIntakesEntity.getDefaultInstance())
                } else {
                    throw throwable
                }
            }
            .firstOrNull() ?: NutritionalIntakesEntity.getDefaultInstance()

    override suspend fun setMinimumNutritionalIntake(nutrient: NutrientEntity) {
        nutritionalIntakeDataStore.updateData {
            it.toBuilder().setMinimum(nutrient).build()
        }
    }

    override suspend fun setMaximumNutritionalIntake(nutrient: NutrientEntity) {
        nutritionalIntakeDataStore.updateData {
            it.toBuilder().setMaximum(nutrient).build()
        }
    }

    //    private companion object {
//        val CALORIES = stringPreferencesKey("calories")
//        val CHOLESTEROL = stringPreferencesKey("cholesterol")
//        val FAT = stringPreferencesKey("fat")
//        val SODIUM = stringPreferencesKey("sodium")
//        val CARBOHYDRATES = stringPreferencesKey("carbohydrates")
//        val FIBER = stringPreferencesKey("fiber")
//        val PROTEIN = stringPreferencesKey("protein")
//        val VIT_A = stringPreferencesKey("vit_a")
//        val VIT_C = stringPreferencesKey("vit_c")
//        val CALCIUM = stringPreferencesKey("calcium")
//        val IRON = stringPreferencesKey("iron")
//    }
}
