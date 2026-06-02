import type { Habit, DayOfWeek } from '../../types/habit'
import { getTodayKey, getToday, getDayOfWeek } from '../../utils/dateUtils'
import { LevelBadge } from '../LevelBadge/LevelBadge'
import styles from './HabitCard.module.css'

interface HabitCardProps {
  habit: Habit
  onToggle: (habitId: string, date: string) => void
  onSelect: (habit: Habit) => void
}

const DAY_ABBREVS: { value: DayOfWeek; label: string }[] = [
  { value: 0, label: 'D' },
  { value: 1, label: 'S' },
  { value: 2, label: 'T' },
  { value: 3, label: 'Q' },
  { value: 4, label: 'Q' },
  { value: 5, label: 'S' },
  { value: 6, label: 'S' },
]

export function HabitCard({ habit, onToggle, onSelect }: HabitCardProps) {
  const todayKey = getTodayKey()
  const today = getToday()
  const todayDow = getDayOfWeek(today)
  const isScheduledToday = habit.selectedDays.includes(todayDow)
  const isDoneToday = habit.completions.some(
    (c) => c.date === todayKey && c.completed
  )

  function handleToggleClick(e: React.MouseEvent | React.KeyboardEvent) {
    e.stopPropagation()
    onToggle(habit.id, todayKey)
  }

  function handleToggleKeyDown(e: React.KeyboardEvent) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault()
      handleToggleClick(e)
    }
  }

  function handleCardClick() {
    onSelect(habit)
  }

  function handleCardKeyDown(e: React.KeyboardEvent) {
    if (e.key === 'Enter') {
      onSelect(habit)
    }
  }

  return (
    <div
      className={`${styles.card} ${!isScheduledToday ? styles.notScheduled : ''}`}
      onClick={handleCardClick}
      onKeyDown={handleCardKeyDown}
      tabIndex={0}
      role="button"
      aria-label={`${habit.name}, nível ${habit.level}`}
    >
      <LevelBadge level={habit.level} size="small" />

      <div className={styles.content}>
        <span className={styles.name}>{habit.name}</span>

        <div className={styles.daysRow}>
          {DAY_ABBREVS.map(({ value, label }) => {
            const isActive = habit.selectedDays.includes(value)
            const isToday = value === todayDow
            return (
              <span
                key={value}
                className={`${styles.dayLabel} ${isActive ? styles.dayActive : ''} ${isToday ? styles.dayToday : ''}`}
              >
                {label}
              </span>
            )
          })}
        </div>

        <div className={styles.streakRow}>
          <span className={styles.streakIcon}>🔥</span>
          <span className={styles.streakValue}>{habit.currentStreak}</span>
          <span>dias</span>
        </div>
      </div>

      <button
        className={`${styles.toggle} ${isDoneToday ? styles.toggleDone : ''}`}
        onClick={handleToggleClick}
        onKeyDown={handleToggleKeyDown}
        tabIndex={0}
        aria-label={isDoneToday ? 'Desmarcar hábito' : 'Marcar hábito como feito'}
        aria-pressed={isDoneToday}
      >
        <svg
          className={styles.checkIcon}
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="3"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <polyline points="20 6 9 17 4 12" />
        </svg>
      </button>
    </div>
  )
}
