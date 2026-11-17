const DATE_TIME = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' })

export function formatDateTime(value: string) {
  return DATE_TIME.format(new Date(value))
}
