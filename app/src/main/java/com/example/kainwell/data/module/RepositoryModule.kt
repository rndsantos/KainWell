package com.example.kainwell.data.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.kainwell.Diets
import com.example.kainwell.NutritionalIntakes
import com.example.kainwell.data.diet.DietsSerializer
import com.example.kainwell.data.diet.SavedDietsRepository
import com.example.kainwell.data.diet.impl.SavedDietsRepositoryImpl
import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.data.food.impl.FoodRepositoryImpl
import com.example.kainwell.data.nutrient.NutritionalIntakeRepository
import com.example.kainwell.data.nutrient.NutritionalIntakeSerializer
import com.example.kainwell.data.nutrient.impl.NutritionalIntakeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.nutritionalIntakeDataStore: DataStore<NutritionalIntakes> by dataStore(
    fileName = "nutritional_intakes.pb",
    serializer = NutritionalIntakeSerializer
)

val Context.savedDietsDataStore: DataStore<Diets> by dataStore(
    fileName = "saved_diets.pb",
    serializer = DietsSerializer
)

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    abstract fun provideNutritionalIntakeRepository(impl: NutritionalIntakeRepositoryImpl): NutritionalIntakeRepository

    @Binds
    abstract fun provideSavedDietsDataStore(impl: SavedDietsRepositoryImpl): SavedDietsRepository

    companion object {
        @Provides
        @Singleton
        fun provideNutritionalIntakeDataStore(
            @ApplicationContext applicationContext: Context,
        ): DataStore<NutritionalIntakes> {
            return applicationContext.nutritionalIntakeDataStore
        }

        @Provides
        @Singleton
        fun provideSavedDietsDataStore(
            @ApplicationContext applicationContext: Context,
        ): DataStore<Diets> {
            return applicationContext.savedDietsDataStore
        }
    }
}