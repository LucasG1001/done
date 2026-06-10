import { pool } from '../db.js'
import { getZonedNow, parseTimeToMinutes } from '../utils/datetime.js'
import { getCompletion, setCompletion } from './completions.js'
import {
  answerCallbackQuery,
  editMessageText,
  isTelegramConfigured,
  sendMessage,
  type TelegramCallbackQuery,
} from './telegram.js'

const DEFAULT_TIME = '21:00'
const MAX_REMINDERS = 5
const REMINDER_INTERVAL_MS = 5 * 60 * 1000

interface HabitScheduleRow {
  id: string
  name: string
  selected_days: number[]
  scheduled_time: string | null
}

interface ReminderRow {
  reminder_count: number
  last_sent_at: string | null
  resolved: boolean
}

function buildButtons(habitId: string, date: string) {
  return [
    [
      { text: '✅ Feito', callbackData: `done:${habitId}:${date}` },
      { text: '❌ Não feito', callbackData: `notdone:${habitId}:${date}` },
    ],
  ]
}

async function getReminder(habitId: string, date: string): Promise<ReminderRow | null> {
  const result = await pool.query<ReminderRow>(
    'SELECT reminder_count, last_sent_at, resolved FROM habit_reminders WHERE habit_id = $1 AND date = $2',
    [habitId, date]
  )
  return result.rows[0] ?? null
}

async function registerSend(habitId: string, date: string, count: number): Promise<void> {
  await pool.query(
    `INSERT INTO habit_reminders (habit_id, date, reminder_count, last_sent_at)
     VALUES ($1, $2, $3, NOW())
     ON CONFLICT (habit_id, date)
     DO UPDATE SET reminder_count = $3, last_sent_at = NOW()`,
    [habitId, date, count]
  )
}

async function resolveReminder(habitId: string, date: string): Promise<void> {
  await pool.query(
    `INSERT INTO habit_reminders (habit_id, date, resolved)
     VALUES ($1, $2, TRUE)
     ON CONFLICT (habit_id, date)
     DO UPDATE SET resolved = TRUE`,
    [habitId, date]
  )
}

async function processHabit(habit: HabitScheduleRow): Promise<void> {
  const now = getZonedNow()

  if (!habit.selected_days.includes(now.dayOfWeek)) return

  const scheduledMinutes = parseTimeToMinutes(habit.scheduled_time ?? DEFAULT_TIME)
  if (now.minutes < scheduledMinutes) return

  const completion = await getCompletion(habit.id, now.dateKey)
  if (completion) {
    await resolveReminder(habit.id, now.dateKey)
    return
  }

  const reminder = await getReminder(habit.id, now.dateKey)
  if (reminder?.resolved) return

  const count = reminder?.reminder_count ?? 0
  const lastSentMs = reminder?.last_sent_at ? new Date(reminder.last_sent_at).getTime() : 0
  const elapsed = Date.now() - lastSentMs
  const intervalReady = !reminder?.last_sent_at || elapsed >= REMINDER_INTERVAL_MS

  if (count >= MAX_REMINDERS) {
    if (intervalReady) {
      await setCompletion(habit.id, now.dateKey, false, { locked: true, force: true })
      await resolveReminder(habit.id, now.dateKey)
      await sendMessage(
        `🔒 <b>${habit.name}</b> foi marcado automaticamente como <b>não feito</b> e não pode ser desfeito.`
      )
    }
    return
  }

  if (!intervalReady) return

  const nextCount = count + 1
  await sendMessage(
    `⏰ <b>${habit.name}</b>\nVocê concluiu este hábito hoje? (lembrete ${nextCount}/${MAX_REMINDERS})`,
    buildButtons(habit.id, now.dateKey)
  )
  await registerSend(habit.id, now.dateKey, nextCount)
}

export async function runReminderTick(): Promise<void> {
  if (!isTelegramConfigured()) return

  const result = await pool.query<HabitScheduleRow>(
    'SELECT id, name, selected_days, scheduled_time FROM habits'
  )

  for (const habit of result.rows) {
    try {
      await processHabit(habit)
    } catch {
      continue
    }
  }
}

export async function handleCallback(callback: TelegramCallbackQuery): Promise<void> {
  const data = callback.data
  if (!data) return

  const [action, habitId, date] = data.split(':')
  if ((action !== 'done' && action !== 'notdone') || !habitId || !date) {
    await answerCallbackQuery(callback.id, 'Ação inválida')
    return
  }

  const existing = await getCompletion(habitId, date)
  if (existing?.locked) {
    await answerCallbackQuery(callback.id, 'Já marcado automaticamente, não pode ser alterado')
    await resolveReminder(habitId, date)
    return
  }

  const completed = action === 'done'
  await setCompletion(habitId, date, completed)
  await resolveReminder(habitId, date)

  const habitResult = await pool.query<{ name: string }>(
    'SELECT name FROM habits WHERE id = $1',
    [habitId]
  )
  const habitName = habitResult.rows[0]?.name ?? 'Hábito'

  await answerCallbackQuery(callback.id, completed ? 'Marcado como feito' : 'Marcado como não feito')

  if (callback.message) {
    const label = completed ? '✅ feito' : '❌ não feito'
    await editMessageText(callback.message.message_id, `<b>${habitName}</b> marcado como ${label}.`)
  }
}
