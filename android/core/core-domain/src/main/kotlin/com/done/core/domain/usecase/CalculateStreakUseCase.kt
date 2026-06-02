package com.done.core.domain.usecase

import com.done.core.domain.repository.HabitRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    data class StreakResult(
        val currentStreak: Int,
        val bestStreak: Int
    )

    suspend operator fun invoke(habitId: Long, checksPerDay: Int): StreakResult {
        val dayCounts = habitRepository.getAllCheckDayCounts(habitId)
        if (dayCounts.isEmpty()) return StreakResult(0, 0)

        val completedDates = dayCounts
            .filter { it.checkCount >= checksPerDay }
            .map { LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE) }
            .sortedDescending()

        if (completedDates.isEmpty()) return StreakResult(0, 0)

        val today = LocalDate.now()
        var currentStreak = 0
        var bestStreak = 0
        var streak = 0
        var expectedDate = today

        // If today is not completed yet, start checking from yesterday
        if (completedDates.firstOrNull() != today) {
            expectedDate = today.minusDays(1)
            // If yesterday is also not completed, current streak is 0
            if (completedDates.firstOrNull() != expectedDate) {
                // Calculate best streak only
                return StreakResult(
                    currentStreak = 0,
                    bestStreak = calculateBestStreak(completedDates)
                )
            }
        }

        for (date in completedDates) {
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date.isBefore(expectedDate)) {
                if (streak > bestStreak) bestStreak = streak
                if (currentStreak == 0) currentStreak = streak
                streak = 1
                expectedDate = date.minusDays(1)
            }
        }

        if (streak > bestStreak) bestStreak = streak
        if (currentStreak == 0) currentStreak = streak

        return StreakResult(currentStreak, bestStreak)
    }

    private fun calculateBestStreak(sortedDates: List<LocalDate>): Int {
        if (sortedDates.isEmpty()) return 0

        var bestStreak = 1
        var currentRun = 1

        for (i in 1 until sortedDates.size) {
            if (sortedDates[i - 1].minusDays(1) == sortedDates[i]) {
                currentRun++
            } else {
                if (currentRun > bestStreak) bestStreak = currentRun
                currentRun = 1
            }
        }

        return maxOf(bestStreak, currentRun)
    }
}
