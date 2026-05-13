package com.done.core.domain.usecase

import com.done.core.domain.model.Habit
import com.done.core.domain.repository.HabitRepository
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    sealed interface Result {
        data object Success : Result
        data class ValidationError(val message: String) : Result
        data object NotFound : Result
    }

    suspend operator fun invoke(
        habitId: Long,
        name: String,
        icon: String,
        color: String,
        checksPerDay: Int
    ): Result {
        if (name.isBlank()) {
            return Result.ValidationError("Nome do hábito não pode ser vazio")
        }
        if (name.length > 50) {
            return Result.ValidationError("Nome deve ter no máximo 50 caracteres")
        }
        if (checksPerDay !in 1..20) {
            return Result.ValidationError("Checks por dia deve ser entre 1 e 20")
        }

        val existing = habitRepository.getHabitById(habitId)
            ?: return Result.NotFound

        habitRepository.updateHabit(
            existing.copy(
                name = name.trim(),
                icon = icon,
                color = color,
                checksPerDay = checksPerDay
            )
        )
        return Result.Success
    }
}
