package com.done.core.domain.usecase

import com.done.core.domain.model.HabitStats
import com.done.core.domain.repository.HabitCheckRepository
import com.done.core.domain.repository.HabitRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetHabitStatsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val checkRepository: HabitCheckRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {
    suspend operator fun invoke(habitId: Long): HabitStats? {
        val habit = habitRepository.getHabitById(habitId) ?: return null
        val streakResult = calculateStreakUseCase(habitId, habit.checksPerDay)
        val totalChecks = checkRepository.getTotalChecks(habitId)
        val dayCounts = habitRepository.getAllCheckDayCounts(habitId)
        val completedDays = dayCounts.count { it.checkCount >= habit.checksPerDay }

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val day30Ago = today.minusDays(30).format(formatter)
        val day90Ago = today.minusDays(90).format(formatter)

        val rate30 = calculateCompletionRate(dayCounts, habit.checksPerDay, day30Ago, 30)
        val rate90 = calculateCompletionRate(dayCounts, habit.checksPerDay, day90Ago, 90)

        return HabitStats(
            habit = habit,
            currentStreak = streakResult.currentStreak,
            bestStreak = streakResult.bestStreak,
            completionRateLast30Days = rate30,
            completionRateLast90Days = rate90,
            totalChecks = totalChecks,
            completedDays = completedDays
        )
    }

    private fun calculateCompletionRate(
        dayCounts: List<com.done.core.domain.model.DayProgress>,
        checksPerDay: Int,
        fromDay: String,
        totalDays: Int
    ): Float {
        val completedInRange = dayCounts
            .filter { it.date >= fromDay && it.checkCount >= checksPerDay }
            .size
        return completedInRange.toFloat() / totalDays
    }
}
