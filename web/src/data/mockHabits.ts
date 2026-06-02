import type { DayOfWeek, Habit, HabitCompletion } from '../types/habit'
import { addDays, formatDateKey, getToday, isScheduledDay } from '../utils/dateUtils'

function generateCompletions(
  selectedDays: DayOfWeek[],
  daysBack: number,
  shouldComplete: (dayIndex: number, scheduledIndex: number) => boolean
): HabitCompletion[] {
  const today = getToday()
  const completions: HabitCompletion[] = []
  let scheduledIndex = 0

  for (let i = daysBack; i >= 0; i--) {
    const date = addDays(today, -i)
    if (isScheduledDay(date, selectedDays)) {
      const completed = shouldComplete(i, scheduledIndex)
      completions.push({
        date: formatDateKey(date),
        completed,
      })
      scheduledIndex++
    }
  }

  return completions
}

function createMeditarHabit(): Habit {
  const selectedDays: DayOfWeek[] = [1, 2, 3, 4, 5]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo <= 45) return true
    if (daysAgo <= 55) return daysAgo % 2 === 0
    return daysAgo % 3 === 0
  })

  return {
    id: 'habit-meditar',
    name: 'Meditar',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

function createExercitarHabit(): Habit {
  const selectedDays: DayOfWeek[] = [1, 3, 5]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo <= 12) return true
    if (daysAgo > 12 && daysAgo <= 20) return false
    if (daysAgo > 20 && daysAgo <= 52) return true
    return daysAgo % 2 === 0
  })

  return {
    id: 'habit-exercitar',
    name: 'Exercitar',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

function createLerHabit(): Habit {
  const selectedDays: DayOfWeek[] = [0, 1, 2, 3, 4, 5, 6]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo <= 3) return true
    if (daysAgo <= 10) return false
    return daysAgo % 4 === 0
  })

  return {
    id: 'habit-ler',
    name: 'Ler 20 minutos',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

function createBeberAguaHabit(): Habit {
  const selectedDays: DayOfWeek[] = [0, 1, 2, 3, 4, 5, 6]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo <= 67) return true
    return daysAgo % 3 === 0
  })

  return {
    id: 'habit-agua',
    name: 'Beber 2L de água',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

function createSemRedesSociaisHabit(): Habit {
  const selectedDays: DayOfWeek[] = [1, 2, 3, 4, 5]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo === 0) return false
    if (daysAgo === 1) return false
    if (daysAgo <= 10) return true
    return daysAgo % 3 !== 0
  })

  return {
    id: 'habit-redes',
    name: 'Sem redes sociais',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

function createEscreverDiarioHabit(): Habit {
  const selectedDays: DayOfWeek[] = [0, 3, 6]

  const completions = generateCompletions(selectedDays, 90, (daysAgo) => {
    if (daysAgo <= 8) return true
    if (daysAgo > 8 && daysAgo <= 15) return false
    if (daysAgo > 15 && daysAgo <= 39) return true
    return daysAgo % 2 === 0
  })

  return {
    id: 'habit-diario',
    name: 'Escrever diário',
    selectedDays,
    completions,
    currentStreak: 0,
    longestStreak: 0,
    level: 1,
    createdAt: formatDateKey(addDays(getToday(), -90)) + 'T08:00:00.000Z',
    updatedAt: new Date().toISOString(),
  }
}

export function createMockHabits(): Habit[] {
  return [
    createMeditarHabit(),
    createExercitarHabit(),
    createLerHabit(),
    createBeberAguaHabit(),
    createSemRedesSociaisHabit(),
    createEscreverDiarioHabit(),
  ]
}
