package com.done.core.database.repository

import com.done.core.database.dao.HabitCheckDao
import com.done.core.database.entity.HabitCheckEntity
import com.done.core.domain.repository.HabitCheckRepository
import javax.inject.Inject

class HabitCheckRepositoryImpl @Inject constructor(
    private val habitCheckDao: HabitCheckDao
) : HabitCheckRepository {

    override suspend fun addCheck(habitId: Long, day: String, timestamp: Long): Long =
        habitCheckDao.insertCheck(
            HabitCheckEntity(
                habitId = habitId,
                dateDay = day,
                checkedAt = timestamp
            )
        )

    override suspend fun getCheckCountForDay(habitId: Long, day: String): Int =
        habitCheckDao.getCheckCountForDay(habitId, day)

    override suspend fun undoLastCheck(habitId: Long, day: String): Boolean {
        val lastCheck = habitCheckDao.getLastCheckForDay(habitId, day) ?: return false
        habitCheckDao.deleteCheck(lastCheck.id)
        return true
    }

    override suspend fun getTotalChecks(habitId: Long): Int =
        habitCheckDao.getTotalChecksForHabit(habitId)

    override suspend fun getDaysWithChecksFrom(habitId: Long, fromDay: String): Int =
        habitCheckDao.getDaysWithChecksFrom(habitId, fromDay)
}
