package com.done.core.database.di

import com.done.core.database.repository.HabitCheckRepositoryImpl
import com.done.core.database.repository.HabitRepositoryImpl
import com.done.core.domain.repository.HabitCheckRepository
import com.done.core.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindHabitCheckRepository(impl: HabitCheckRepositoryImpl): HabitCheckRepository
}
