package com.done.core.domain.usecase

import com.done.core.domain.repository.HabitCheckRepository
import com.done.core.domain.repository.HabitRepository
import javax.inject.Inject

class CheckHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val checkRepository: HabitCheckRepository
) {
    sealed interface Result {
        data object Success : Result
        data object AlreadyCompleted : Result
        data object HabitNotFound : Result
    }

    suspend operator fun invoke(habitId: Long, day: String, timestamp: Long): Result {
        val habit = habitRepository.getHabitById(habitId)
            ?: return Result.HabitNotFound

        val currentChecks = checkRepository.getCheckCountForDay(habitId, day)
        if (currentChecks >= habit.checksPerDay) {
            return Result.AlreadyCompleted
        }

        checkRepository.addCheck(habitId, day, timestamp)
        return Result.Success
    }
}
