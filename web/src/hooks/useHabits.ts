import { useState } from 'react'
import type { Habit, HabitFormData } from '../types/habit'
import { createMockHabits } from '../data/mockHabits'
import { calculateCurrentStreak, calculateLongestStreak } from '../utils/streakUtils'
import { calculateLevel } from '../utils/levelUtils'

interface UseHabitsReturn {
  habits: Habit[]
  createHabit: (data: HabitFormData) => void
  updateHabit: (id: string, data: HabitFormData) => void
  deleteHabit: (id: string) => void
  toggleCompletion: (habitId: string, date: string) => void
}

function recalculateHabitStats(habit: Habit): Habit {
  const currentStreak = calculateCurrentStreak(habit.completions, habit.selectedDays)
  const longestStreak = calculateLongestStreak(habit.completions, habit.selectedDays, habit.createdAt)
  const level = calculateLevel(longestStreak, habit.completions, habit.selectedDays)

  return {
    ...habit,
    currentStreak,
    longestStreak,
    level,
  }
}

function initializeHabits(): Habit[] {
  return createMockHabits().map(recalculateHabitStats)
}

export function useHabits(): UseHabitsReturn {
  const [habits, setHabits] = useState<Habit[]>(initializeHabits)

  function createHabit(data: HabitFormData): void {
    const now = new Date().toISOString()
    const newHabit: Habit = {
      id: crypto.randomUUID(),
      name: data.name,
      selectedDays: data.selectedDays,
      completions: [],
      currentStreak: 0,
      longestStreak: 0,
      level: 1,
      createdAt: now,
      updatedAt: now,
    }

    setHabits((prev) => [...prev, newHabit])
  }

  function updateHabit(id: string, data: HabitFormData): void {
    setHabits((prev) =>
      prev.map((habit) => {
        if (habit.id !== id) return habit
        return recalculateHabitStats({
          ...habit,
          name: data.name,
          selectedDays: data.selectedDays,
          updatedAt: new Date().toISOString(),
        })
      })
    )
  }

  function deleteHabit(id: string): void {
    setHabits((prev) => prev.filter((habit) => habit.id !== id))
  }

  function toggleCompletion(habitId: string, date: string): void {
    setHabits((prev) =>
      prev.map((habit) => {
        if (habit.id !== habitId) return habit

        const existingIndex = habit.completions.findIndex((c) => c.date === date)
        let updatedCompletions = [...habit.completions]

        if (existingIndex >= 0) {
          const existing = updatedCompletions[existingIndex]!
          updatedCompletions[existingIndex] = {
            ...existing,
            completed: !existing.completed,
          }
        } else {
          updatedCompletions.push({ date, completed: true })
        }

        return recalculateHabitStats({
          ...habit,
          completions: updatedCompletions,
          updatedAt: new Date().toISOString(),
        })
      })
    )
  }

  return {
    habits,
    createHabit,
    updateHabit,
    deleteHabit,
    toggleCompletion,
  }
}
