package com.done.core.domain.repository

interface HabitCheckRepository {
    suspend fun addCheck(habitId: Long, day: String, timestamp: Long): Long
    suspend fun getCheckCountForDay(habitId: Long, day: String): Int
    suspend fun undoLastCheck(habitId: Long, day: String): Boolean
    suspend fun getTotalChecks(habitId: Long): Int
    suspend fun getDaysWithChecksFrom(habitId: Long, fromDay: String): Int
}
