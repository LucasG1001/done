package com.done.core.domain.usecase

import com.done.core.domain.repository.HabitCheckRepository
import javax.inject.Inject

class UndoLastCheckUseCase @Inject constructor(
    private val checkRepository: HabitCheckRepository
) {
    suspend operator fun invoke(habitId: Long, day: String): Boolean =
        checkRepository.undoLastCheck(habitId, day)
}
