package com.done.feature.today.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.done.core.domain.usecase.CheckHabitUseCase
import com.done.core.domain.usecase.GetTodayHabitsUseCase
import com.done.core.domain.usecase.UndoLastCheckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val getTodayHabits: GetTodayHabitsUseCase,
    private val checkHabit: CheckHabitUseCase,
    private val undoLastCheck: UndoLastCheckUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TodayUiState())
    val state: StateFlow<TodayUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TodayUiEvent>()
    val events: SharedFlow<TodayUiEvent> = _events.asSharedFlow()

    private val today: String
        get() = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    init {
        observeHabits()
    }

    fun onAction(action: TodayUiAction) {
        when (action) {
            is TodayUiAction.CheckHabit -> performCheck(action.habitId)
            is TodayUiAction.UndoCheck -> performUndo(action.habitId)
            is TodayUiAction.OpenDetail -> viewModelScope.launch {
                _events.emit(TodayUiEvent.NavigateToDetail(action.habitId))
            }
        }
    }

    private fun observeHabits() {
        viewModelScope.launch {
            getTodayHabits(today)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { habits ->
                    val completed = habits.count { it.isCompletedToday }
                    val total = habits.size
                    val overallProgress = if (total > 0) completed.toFloat() / total else 0f
                    val avgStreak = if (habits.isNotEmpty()) {
                        habits.sumOf { it.currentStreak } / habits.size
                    } else 0
                    val bestRecord = habits.maxOfOrNull { it.bestStreak } ?: 0

                    _state.update {
                        it.copy(
                            habits = habits,
                            overallProgress = overallProgress,
                            completedCount = completed,
                            averageStreak = avgStreak,
                            bestRecord = bestRecord,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun performCheck(habitId: Long) {
        viewModelScope.launch {
            val result = checkHabit(
                habitId = habitId,
                day = today,
                timestamp = System.currentTimeMillis()
            )
            when (result) {
                is CheckHabitUseCase.Result.AlreadyCompleted -> {
                    _events.emit(TodayUiEvent.ShowError("Hábito já completo hoje"))
                }
                is CheckHabitUseCase.Result.HabitNotFound -> {
                    _events.emit(TodayUiEvent.ShowError("Hábito não encontrado"))
                }
                is CheckHabitUseCase.Result.Success -> { /* Flow auto-updates */ }
            }
        }
    }

    private fun performUndo(habitId: Long) {
        viewModelScope.launch {
            val undone = undoLastCheck(habitId, today)
            if (undone) {
                _events.emit(TodayUiEvent.ShowUndoSnackbar)
            }
        }
    }
}
