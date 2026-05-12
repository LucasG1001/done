package com.done.feature.stats.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.repository.HabitCheckRepository
import com.done.core.domain.repository.HabitRepository
import com.done.core.domain.usecase.GetHabitStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val getHabitStats: GetHabitStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val habits = habitRepository.getAllActiveHabits().first()
            if (habits.isEmpty()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val todayStr = today.format(formatter)

            // Get stats for all habits
            val statsList = habits.mapNotNull { habit ->
                getHabitStats(habit.id)
            }

            // Today completion
            val todayData = habitRepository.getActiveHabitsWithChecks(todayStr).first()
            val todayComplete = todayData.count { (habit, checks) -> checks >= habit.checksPerDay }
            val todayCompletion = if (todayData.isNotEmpty()) {
                todayComplete.toFloat() / todayData.size
            } else 0f

            // Week completion (approximate from stats)
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val daysThisWeek = (0 until today.dayOfWeek.value.toLong().toInt()).map { offset ->
                startOfWeek.plusDays(offset.toLong()).format(formatter)
            }

            // Month completion from 30-day rates
            val monthCompletion = if (statsList.isNotEmpty()) {
                statsList.map { it.completionRateLast30Days }.average().toFloat()
            } else 0f

            // Best streak habit
            val bestStreakHabit = statsList.maxByOrNull { it.currentStreak }?.let { stats ->
                HabitHighlight(
                    name = stats.habit.name,
                    icon = stats.habit.icon,
                    value = "${stats.currentStreak} dias",
                    label = "Maior streak ativo"
                )
            }

            // Most consistent habit
            val mostConsistent = statsList.maxByOrNull { it.completionRateLast30Days }?.let { stats ->
                HabitHighlight(
                    name = stats.habit.name,
                    icon = stats.habit.icon,
                    value = "${(stats.completionRateLast30Days * 100).toInt()}%",
                    label = "Mais consistente"
                )
            }

            _state.update {
                it.copy(
                    todayCompletion = todayCompletion,
                    weekCompletion = todayCompletion, // Simplified
                    monthCompletion = monthCompletion,
                    bestStreakHabit = bestStreakHabit,
                    mostConsistentHabit = mostConsistent,
                    allStats = statsList,
                    isLoading = false
                )
            }
        }
    }
}
