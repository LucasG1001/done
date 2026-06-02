package com.done.core.domain.usecase

import com.done.core.domain.model.Habit
import com.done.core.domain.repository.HabitRepository
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    sealed interface Result {
        data class Success(val id: Long) : Result
        data class ValidationError(val message: String) : Result
    }

    suspend operator fun invoke(
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

        val sortOrder = habitRepository.getMaxSortOrder() + 1
        val habit = Habit(
            name = name.trim(),
            icon = icon,
            color = color,
            checksPerDay = checksPerDay,
            createdAt = System.currentTimeMillis(),
            sortOrder = sortOrder
        )
        val id = habitRepository.createHabit(habit)
        return Result.Success(id)
    }
}
