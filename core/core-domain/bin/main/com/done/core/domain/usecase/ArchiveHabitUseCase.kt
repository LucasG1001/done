package com.done.core.domain.usecase

import com.done.core.domain.repository.HabitRepository
import javax.inject.Inject

class ArchiveHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long) {
        habitRepository.archiveHabit(habitId)
    }
}
