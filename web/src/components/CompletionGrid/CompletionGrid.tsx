import type { DayOfWeek, HabitCompletion } from '../../types/habit'
import { addDays, formatDateBR, formatDateKey, getToday, isScheduledDay } from '../../utils/dateUtils'
import styles from './CompletionGrid.module.css'

interface CompletionGridProps {
  completions: HabitCompletion[]
  selectedDays: DayOfWeek[]
}

function getDotState(
  dateKey: string,
  date: Date,
  completions: HabitCompletion[],
  selectedDays: DayOfWeek[]
): 'completed' | 'missed' | 'notScheduled' {
  if (!isScheduledDay(date, selectedDays)) {
    return 'notScheduled'
  }

  const completion = completions.find((c) => c.date === dateKey)
  if (completion?.completed) {
    return 'completed'
  }

  return 'missed'
}

export function CompletionGrid({ completions, selectedDays }: CompletionGridProps) {
  const today = getToday()
  const days = Array.from({ length: 30 }, (_, i) => {
    const date = addDays(today, -(29 - i))
    const dateKey = formatDateKey(date)
    const state = getDotState(dateKey, date, completions, selectedDays)
    return { date, dateKey, state }
  })

  return (
    <div className={styles.grid}>
      {days.map(({ dateKey, state }) => (
        <div
          key={dateKey}
          className={`${styles.dot} ${styles[state]}`}
        >
          <span className={styles.tooltip}>{formatDateBR(dateKey)}</span>
        </div>
      ))}
    </div>
  )
}
