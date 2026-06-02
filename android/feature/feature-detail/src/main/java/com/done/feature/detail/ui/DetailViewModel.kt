package com.done.feature.detail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.model.DayProgress
import com.done.core.domain.repository.HabitRepository
import com.done.core.domain.usecase.ArchiveHabitUseCase
import com.done.core.domain.usecase.GetHabitStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getHabitStats: GetHabitStatsUseCase,
    private val archiveHabit: ArchiveHabitUseCase,
    private val habitRepository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: Long = savedStateHandle.get<Long>("habitId") ?: 0L

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<DetailUiEvent>()
    val events: SharedFlow<DetailUiEvent> = _events.asSharedFlow()

    init {
        loadData()
    }

    fun onAction(action: DetailUiAction) {
        when (action) {
            DetailUiAction.Edit -> viewModelScope.launch {
                _events.emit(DetailUiEvent.NavigateToEdit(habitId))
            }
            DetailUiAction.Archive -> viewModelScope.launch {
                archiveHabit(habitId)
                _events.emit(DetailUiEvent.Archived)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val stats = getHabitStats(habitId) ?: return@launch

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val from90 = today.minusDays(90).format(formatter)

            val allCounts = habitRepository.getAllCheckDayCounts(habitId)
            val countsMap = allCounts.associate { it.date to it.checkCount }

            // Build contribution grid for last 91 days
            val contributionDays = (0L..90L).map { daysAgo ->
                val date = today.minusDays(daysAgo)
                val dateStr = date.format(formatter)
                ContributionDayUi(
                    date = dateStr,
                    checkCount = countsMap[dateStr] ?: 0,
                    checksPerDay = stats.habit.checksPerDay
                )
            }.reversed()

            // Last 14 days for bar chart
            val recentDays = (0L..13L).map { daysAgo ->
                val date = today.minusDays(daysAgo)
                val dateStr = date.format(formatter)
                DayProgress(
                    date = dateStr,
                    checkCount = countsMap[dateStr] ?: 0
                )
            }.reversed()

            _state.update {
                it.copy(
                    habit = stats.habit,
                    currentStreak = stats.currentStreak,
                    bestStreak = stats.bestStreak,
                    completionRate30Days = stats.completionRateLast30Days,
                    totalChecks = stats.totalChecks,
                    recentDays = recentDays,
                    contributionDays = contributionDays,
                    isLoading = false
                )
            }
        }
    }
}
