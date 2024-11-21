package com.example.kainwell.data.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.kainwell.NutritionalIntakes
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

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    abstract fun provideNutritionalIntakeRepository(impl: NutritionalIntakeRepositoryImpl): NutritionalIntakeRepository

    companion object {
        @Provides
        @Singleton
        fun provideNutritionalIntakeDataStore(
            @ApplicationContext applicationContext: Context,
        ): DataStore<NutritionalIntakes> {
            return applicationContext.nutritionalIntakeDataStore
        }
    }
}