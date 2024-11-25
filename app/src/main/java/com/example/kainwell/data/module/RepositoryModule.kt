package com.example.kainwell.data.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.kainwell.DietsEntity
import com.example.kainwell.NutritionalIntakesEntity
import com.example.kainwell.data.diet.DietsEntitySerializer
import com.example.kainwell.data.diet.SavedDietsRepository
import com.example.kainwell.data.diet.impl.SavedDietsRepositoryImpl
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.data.food.impl.FoodRepositoryImpl
import com.example.kainwell.data.nutrient.NutritionalIntakesEntitySerializer
import com.example.kainwell.data.nutrient.NutritionalIntakesRepository
import com.example.kainwell.data.nutrient.impl.NutritionalIntakesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.nutritionalIntakeDataStore: DataStore<NutritionalIntakesEntity> by dataStore(
    fileName = "nutritional_intakes.pb",
    serializer = NutritionalIntakesEntitySerializer
)

val Context.savedDietsDataStore: DataStore<DietsEntity> by dataStore(
    fileName = "saved_diets.pb",
    serializer = DietsEntitySerializer
)

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    abstract fun provideNutritionalIntakeRepository(impl: NutritionalIntakesRepositoryImpl): NutritionalIntakesRepository

    @Binds
    abstract fun provideSavedDietsDataStore(impl: SavedDietsRepositoryImpl): SavedDietsRepository

    companion object {
        @Provides
        @Singleton
        fun provideNutritionalIntakeDataStore(
            @ApplicationContext applicationContext: Context,
        ): DataStore<NutritionalIntakesEntity> {
            return applicationContext.nutritionalIntakeDataStore
        }

        @Provides
        @Singleton
        fun provideSavedDietsDataStore(
            @ApplicationContext applicationContext: Context,
        ): DataStore<DietsEntity> {
            return applicationContext.savedDietsDataStore
        }
    }
}