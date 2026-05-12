package com.done.core.domain.usecase

import com.done.core.domain.model.DayProgress
import com.done.core.testing.FakeHabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalculateStreakUseCaseTest {

    private lateinit var repository: FakeHabitRepository
    private lateinit var useCase: CalculateStreakUseCase

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val today = LocalDate.now()

    @BeforeEach
    fun setup() {
        repository = FakeHabitRepository()
        useCase = CalculateStreakUseCase(repository)
    }

    private fun daysAgo(n: Long): String = today.minusDays(n).format(formatter)

    @Nested
    @DisplayName("Streak com dias consecutivos")
    inner class ConsecutiveDays {

        @Test
        fun `streak de 5 dias consecutivos incluindo hoje`() = runTest {
            val counts = (0L..4L).map { DayProgress(daysAgo(it), 1) }
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 1)

            assertEquals(5, result.currentStreak)
            assertEquals(5, result.bestStreak)
        }

        @Test
        fun `streak conta a partir de ontem se hoje nao esta completo`() = runTest {
            val counts = (1L..5L).map { DayProgress(daysAgo(it), 1) }
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 1)

            assertEquals(5, result.currentStreak)
        }
    }

    @Nested
    @DisplayName("Streak com lacunas")
    inner class WithGaps {

        @Test
        fun `lacuna quebra o streak atual`() = runTest {
            val counts = listOf(
                DayProgress(daysAgo(0), 1),
                DayProgress(daysAgo(1), 1),
                // gap at day 2
                DayProgress(daysAgo(3), 1),
                DayProgress(daysAgo(4), 1),
                DayProgress(daysAgo(5), 1)
            )
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 1)

            assertEquals(2, result.currentStreak)
            assertEquals(3, result.bestStreak)
        }

        @Test
        fun `streak zero quando nenhum dia recente completo`() = runTest {
            val counts = listOf(
                DayProgress(daysAgo(10), 1),
                DayProgress(daysAgo(11), 1)
            )
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 1)

            assertEquals(0, result.currentStreak)
            assertEquals(2, result.bestStreak)
        }
    }

    @Nested
    @DisplayName("Multi-check parcial")
    inner class MultiCheckPartial {

        @Test
        fun `dia parcial nao conta para streak`() = runTest {
            val counts = listOf(
                DayProgress(daysAgo(0), 3),
                DayProgress(daysAgo(1), 2), // checksPerDay = 3, partial
                DayProgress(daysAgo(2), 3)
            )
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 3)

            assertEquals(1, result.currentStreak)
        }

        @Test
        fun `todos os dias completos com multi-check`() = runTest {
            val counts = (0L..6L).map { DayProgress(daysAgo(it), 5) }
            repository.setDayCounts(1L, counts)

            val result = useCase(1L, 5)

            assertEquals(7, result.currentStreak)
            assertEquals(7, result.bestStreak)
        }
    }

    @Nested
    @DisplayName("Casos limite")
    inner class EdgeCases {

        @Test
        fun `sem nenhum check retorna streak zero`() = runTest {
            repository.setDayCounts(1L, emptyList())

            val result = useCase(1L, 1)

            assertEquals(0, result.currentStreak)
            assertEquals(0, result.bestStreak)
        }

        @Test
        fun `apenas um dia completo`() = runTest {
            repository.setDayCounts(1L, listOf(DayProgress(daysAgo(0), 1)))

            val result = useCase(1L, 1)

            assertEquals(1, result.currentStreak)
            assertEquals(1, result.bestStreak)
        }
    }
}
