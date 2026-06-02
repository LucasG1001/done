package com.done.core.domain.repository

import com.done.core.domain.model.DayProgress
import com.done.core.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getActiveHabitsWithChecks(day: String): Flow<List<Pair<Habit, Int>>>
    fun observeHabit(id: Long): Flow<Habit?>
    fun getAllActiveHabits(): Flow<List<Habit>>
    suspend fun getHabitById(id: Long): Habit?
    suspend fun createHabit(habit: Habit): Long
    suspend fun updateHabit(habit: Habit)
    suspend fun archiveHabit(id: Long)
    suspend fun getCheckDays(habitId: Long): List<String>
    suspend fun getAllCheckDayCounts(habitId: Long): List<DayProgress>
    fun getCheckHistoryFrom(habitId: Long, fromDay: String): Flow<List<DayProgress>>
    suspend fun getMaxSortOrder(): Int
}
