package com.example.kainwell.data.module

import com.example.kainwell.data.food.FoodRepository
import com.example.kainwell.data.food.impl.FoodRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideFoodRepository(impl: FoodRepositoryImpl): FoodRepository
}