package com.done.core.domain.usecase

import com.done.core.domain.model.Habit
import com.done.core.testing.FakeHabitCheckRepository
import com.done.core.testing.FakeHabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CheckHabitUseCaseTest {

    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var checkRepository: FakeHabitCheckRepository
    private lateinit var useCase: CheckHabitUseCase

    private val testHabit = Habit(
        id = 1L,
        name = "Test",
        icon = "💪",
        color = "#1D9E75",
        checksPerDay = 3,
        createdAt = System.currentTimeMillis()
    )

    @BeforeEach
    fun setup() {
        habitRepository = FakeHabitRepository()
        checkRepository = FakeHabitCheckRepository()
        useCase = CheckHabitUseCase(habitRepository, checkRepository)
        habitRepository.setHabits(listOf(testHabit))
    }

    @Test
    @DisplayName("Check com sucesso incrementa contador")
    fun `check succeeds when under limit`() = runTest {
        val result = useCase(1L, "2025-01-01", System.currentTimeMillis())

        assertTrue(result is CheckHabitUseCase.Result.Success)
        assertEquals(1, checkRepository.getCheckCountForDay(1L, "2025-01-01"))
    }

    @Test
    @DisplayName("Check falha quando checksPerDay atingido")
    fun `check fails when already completed`() = runTest {
        checkRepository.setChecks(1L, "2025-01-01", listOf(1L, 2L, 3L))

        val result = useCase(1L, "2025-01-01", System.currentTimeMillis())

        assertTrue(result is CheckHabitUseCase.Result.AlreadyCompleted)
    }

    @Test
    @DisplayName("Check falha para habito inexistente")
    fun `check fails for nonexistent habit`() = runTest {
        val result = useCase(999L, "2025-01-01", System.currentTimeMillis())

        assertTrue(result is CheckHabitUseCase.Result.HabitNotFound)
    }

    @Test
    @DisplayName("Multiplos checks ate o limite")
    fun `multiple checks up to limit`() = runTest {
        repeat(3) {
            useCase(1L, "2025-01-01", System.currentTimeMillis())
        }

        assertEquals(3, checkRepository.getCheckCountForDay(1L, "2025-01-01"))

        val extraResult = useCase(1L, "2025-01-01", System.currentTimeMillis())
        assertTrue(extraResult is CheckHabitUseCase.Result.AlreadyCompleted)
    }
}
