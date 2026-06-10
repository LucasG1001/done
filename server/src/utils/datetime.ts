const TIME_ZONE = process.env.APP_TIMEZONE ?? 'America/Sao_Paulo'

interface ZonedNow {
  dateKey: string
  dayOfWeek: number
  minutes: number
}

export function getZonedNow(now: Date = new Date()): ZonedNow {
  const formatter = new Intl.DateTimeFormat('en-CA', {
    timeZone: TIME_ZONE,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
    weekday: 'short',
  })

  const parts = formatter.formatToParts(now)
  const lookup = (type: string): string =>
    parts.find((part) => part.type === type)?.value ?? ''

  const year = lookup('year')
  const month = lookup('month')
  const day = lookup('day')
  const hour = lookup('hour') === '24' ? '00' : lookup('hour')
  const minute = lookup('minute')

  const weekdayMap: Record<string, number> = {
    Sun: 0,
    Mon: 1,
    Tue: 2,
    Wed: 3,
    Thu: 4,
    Fri: 5,
    Sat: 6,
  }

  return {
    dateKey: `${year}-${month}-${day}`,
    dayOfWeek: weekdayMap[lookup('weekday')] ?? 0,
    minutes: Number(hour) * 60 + Number(minute),
  }
}

export function parseTimeToMinutes(time: string): number {
  const [hours, minutes] = time.split(':').map(Number)
  return (hours ?? 0) * 60 + (minutes ?? 0)
}

export function isValidTime(time: string): boolean {
  if (!/^\d{2}:\d{2}$/.test(time)) return false
  const [hours, minutes] = time.split(':').map(Number)
  return hours! >= 0 && hours! <= 23 && minutes! >= 0 && minutes! <= 59
}
