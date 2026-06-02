package com.done.core.database.repository

import com.done.core.database.dao.HabitDao
import com.done.core.database.entity.HabitEntity
import com.done.core.domain.model.DayProgress
import com.done.core.domain.model.Habit
import com.done.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getActiveHabitsWithChecks(day: String): Flow<List<Pair<Habit, Int>>> =
        habitDao.getHabitsWithChecksForDay(day).map { list ->
            list.map { entity ->
                Habit(
                    id = entity.id,
                    name = entity.name,
                    icon = entity.icon,
                    color = entity.color,
                    checksPerDay = entity.checksPerDay,
                    createdAt = entity.createdAt,
                    isArchived = entity.isArchived,
                    sortOrder = entity.sortOrder
                ) to entity.checksToday
            }
        }

    override fun observeHabit(id: Long): Flow<Habit?> =
        habitDao.observeHabitById(id).map { it?.toDomain() }

    override fun getAllActiveHabits(): Flow<List<Habit>> =
        habitDao.getAllActiveHabits().map { list -> list.map { it.toDomain() } }

    override suspend fun getHabitById(id: Long): Habit? =
        habitDao.getHabitById(id)?.toDomain()

    override suspend fun createHabit(habit: Habit): Long =
        habitDao.insertHabit(habit.toEntity())

    override suspend fun updateHabit(habit: Habit) =
        habitDao.updateHabit(habit.toEntity())

    override suspend fun archiveHabit(id: Long) =
        habitDao.setArchived(id, true)

    override suspend fun getCheckDays(habitId: Long): List<String> =
        habitDao.getCheckDays(habitId)

    override suspend fun getAllCheckDayCounts(habitId: Long): List<DayProgress> =
        habitDao.getAllCheckDayCounts(habitId).map { DayProgress(it.dateDay, it.count) }

    override fun getCheckHistoryFrom(habitId: Long, fromDay: String): Flow<List<DayProgress>> =
        habitDao.getCheckHistoryFrom(habitId, fromDay).map { list ->
            list.map { DayProgress(it.dateDay, it.count) }
        }

    override suspend fun getMaxSortOrder(): Int =
        habitDao.getMaxSortOrder() ?: 0

    private fun HabitEntity.toDomain() = Habit(
        id = id, name = name, icon = icon, color = color,
        checksPerDay = checksPerDay, createdAt = createdAt,
        isArchived = isArchived, sortOrder = sortOrder
    )

    private fun Habit.toEntity() = HabitEntity(
        id = id, name = name, icon = icon, color = color,
        checksPerDay = checksPerDay, createdAt = createdAt,
        isArchived = isArchived, sortOrder = sortOrder
    )
}
