package com.done.core.testing

import com.done.core.domain.repository.HabitCheckRepository

class FakeHabitCheckRepository : HabitCheckRepository {

    private val checks = mutableMapOf<Pair<Long, String>, MutableList<Long>>()
    private var nextId = 1L

    fun setChecks(habitId: Long, day: String, timestamps: List<Long>) {
        checks[habitId to day] = timestamps.toMutableList()
    }

    override suspend fun addCheck(habitId: Long, day: String, timestamp: Long): Long {
        val key = habitId to day
        checks.getOrPut(key) { mutableListOf() }.add(timestamp)
        return nextId++
    }

    override suspend fun getCheckCountForDay(habitId: Long, day: String): Int =
        checks[habitId to day]?.size ?: 0

    override suspend fun undoLastCheck(habitId: Long, day: String): Boolean {
        val list = checks[habitId to day] ?: return false
        if (list.isEmpty()) return false
        list.removeAt(list.lastIndex)
        return true
    }

    override suspend fun getTotalChecks(habitId: Long): Int =
        checks.filter { it.key.first == habitId }.values.sumOf { it.size }

    override suspend fun getDaysWithChecksFrom(habitId: Long, fromDay: String): Int =
        checks.filter { it.key.first == habitId && it.key.second >= fromDay }
            .count { it.value.isNotEmpty() }
}
