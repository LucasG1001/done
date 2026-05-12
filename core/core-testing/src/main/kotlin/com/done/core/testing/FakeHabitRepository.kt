package com.done.core.testing

import com.done.core.domain.model.DayProgress
import com.done.core.domain.model.Habit
import com.done.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeHabitRepository : HabitRepository {

    private val habits = MutableStateFlow<List<Habit>>(emptyList())
    private val checkCounts = mutableMapOf<Pair<Long, String>, Int>()
    private val dayCounts = mutableMapOf<Long, List<DayProgress>>()

    fun setHabits(list: List<Habit>) {
        habits.value = list
    }

    fun setCheckCount(habitId: Long, day: String, count: Int) {
        checkCounts[habitId to day] = count
    }

    fun setDayCounts(habitId: Long, counts: List<DayProgress>) {
        dayCounts[habitId] = counts
    }

    override fun getActiveHabitsWithChecks(day: String): Flow<List<Pair<Habit, Int>>> =
        habits.map { list ->
            list.filter { !it.isArchived }.map { habit ->
                habit to (checkCounts[habit.id to day] ?: 0)
            }
        }

    override fun observeHabit(id: Long): Flow<Habit?> =
        habits.map { list -> list.find { it.id == id } }

    override fun getAllActiveHabits(): Flow<List<Habit>> =
        habits.map { list -> list.filter { !it.isArchived } }

    override suspend fun getHabitById(id: Long): Habit? =
        habits.value.find { it.id == id }

    override suspend fun createHabit(habit: Habit): Long {
        val newId = (habits.value.maxOfOrNull { it.id } ?: 0) + 1
        habits.value = habits.value + habit.copy(id = newId)
        return newId
    }

    override suspend fun updateHabit(habit: Habit) {
        habits.value = habits.value.map { if (it.id == habit.id) habit else it }
    }

    override suspend fun archiveHabit(id: Long) {
        habits.value = habits.value.map {
            if (it.id == id) it.copy(isArchived = true) else it
        }
    }

    override suspend fun getCheckDays(habitId: Long): List<String> =
        dayCounts[habitId]?.map { it.date } ?: emptyList()

    override suspend fun getAllCheckDayCounts(habitId: Long): List<DayProgress> =
        dayCounts[habitId] ?: emptyList()

    override fun getCheckHistoryFrom(habitId: Long, fromDay: String): Flow<List<DayProgress>> =
        MutableStateFlow(dayCounts[habitId]?.filter { it.date >= fromDay } ?: emptyList())

    override suspend fun getMaxSortOrder(): Int =
        habits.value.maxOfOrNull { it.sortOrder } ?: 0
}
