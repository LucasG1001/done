export type DayOfWeek = 0 | 1 | 2 | 3 | 4 | 5 | 6

export interface HabitCompletion {
  date: string
  completed: boolean
}

export interface Habit {
  id: string
  name: string
  selectedDays: DayOfWeek[]
  completions: HabitCompletion[]
  currentStreak: number
  longestStreak: number
  level: number
  createdAt: string
  updatedAt: string
}

export interface HabitFormData {
  name: string
  selectedDays: DayOfWeek[]
}
