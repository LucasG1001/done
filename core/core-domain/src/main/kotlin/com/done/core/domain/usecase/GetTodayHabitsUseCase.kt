package com.done.core.domain.usecase

import com.done.core.domain.model.Habit
import com.done.core.domain.model.HabitWithProgress
import com.done.core.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTodayHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) {
    operator fun invoke(today: String): Flow<List<HabitWithProgress>> =
        habitRepository.getActiveHabitsWithChecks(today).map { habitsWithChecks ->
            habitsWithChecks.map { (habit, checksToday) ->
                val streakResult = calculateStreakUseCase(habit.id, habit.checksPerDay)
                HabitWithProgress(
                    habit = habit,
                    checksToday = checksToday,
                    isCompletedToday = checksToday >= habit.checksPerDay,
                    currentStreak = streakResult.currentStreak,
                    bestStreak = streakResult.bestStreak
                )
            }
        }
}
