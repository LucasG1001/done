package com.done.core.database.di

import android.content.Context
import androidx.room.Room
import com.done.core.database.DoneDatabase
import com.done.core.database.dao.HabitCheckDao
import com.done.core.database.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DoneDatabase =
        Room.databaseBuilder(
            context,
            DoneDatabase::class.java,
            "done_habits.db"
        ).build()

    @Provides
    fun provideHabitDao(database: DoneDatabase): HabitDao = database.habitDao()

    @Provides
    fun provideHabitCheckDao(database: DoneDatabase): HabitCheckDao = database.habitCheckDao()
}
