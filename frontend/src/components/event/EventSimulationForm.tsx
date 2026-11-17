import { type FormEvent, useState } from 'react'
import { ApiError } from '../../axios/axiosClient'
import { useCategories } from '../../hooks/category/useCategories'
import { useSimulateEvent } from '../../hooks/event/useSimulateEvent'
import { toDateTimeLocal } from '../../utils/formatDate'
import { fieldInputClass } from '../../utils/formStyles'
import { Section } from '../layout/PageLayout'
import { Button } from '../ui/Button'
import { FormField } from '../ui/FormField'

export function EventSimulationForm() {
  const { data: categories = [] } = useCategories()
  const simulateEvent = useSimulateEvent()

  const [categoryId, setCategoryId] = useState('')
  const [occurredAt, setOccurredAt] = useState(() => toDateTimeLocal(new Date()))
  const [title, setTitle] = useState('')
  const [summary, setSummary] = useState('')
  const [url, setUrl] = useState('')
  const [sentTitle, setSentTitle] = useState<string | null>(null)

  const error = simulateEvent.error instanceof ApiError ? simulateEvent.error : undefined
  const fieldErrors = error?.fieldErrors ?? {}
  const generalError = error && Object.keys(fieldErrors).length === 0 ? error.message : undefined

  function clear() {
    setOccurredAt(toDateTimeLocal(new Date()))
    setTitle('')
    setSummary('')
    setUrl('')
    setSentTitle(null)
    simulateEvent.reset()
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSentTitle(null)
    simulateEvent.mutate(
      {
        categoryId: categoryId ? Number(categoryId) : null,
        title: title.trim(),
        summary: summary.trim() || null,
        url: url.trim() || null,
        occurredAt: occurredAt ? new Date(occurredAt).toISOString() : null,
      },
      {
        onSuccess: (sent) => {
          clear()
          setSentTitle(sent.title)
        },
      },
    )
  }

  return (
    <Section title="New event">
      <form onSubmit={handleSubmit} noValidate>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Category" htmlFor="event-category" error={fieldErrors.categoryId}>
            <select
              id="event-category"
              className={fieldInputClass(fieldErrors.categoryId)}
              value={categoryId}
              onChange={(event) => setCategoryId(event.target.value)}
            >
              <option value="">Select a category</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </FormField>
          <FormField label="Occurred at" htmlFor="event-occurred-at" error={fieldErrors.occurredAt}>
            <input
              id="event-occurred-at"
              type="datetime-local"
              className={fieldInputClass(fieldErrors.occurredAt)}
              value={occurredAt}
              onChange={(event) => setOccurredAt(event.target.value)}
            />
          </FormField>
          <FormField label="Title" htmlFor="event-title" error={fieldErrors.title} className="col-span-2">
            <input
              id="event-title"
              className={fieldInputClass(fieldErrors.title)}
              value={title}
              maxLength={255}
              onChange={(event) => setTitle(event.target.value)}
            />
          </FormField>
          <FormField label="Summary" htmlFor="event-summary" error={fieldErrors.summary} className="col-span-2">
            <textarea
              id="event-summary"
              className={`${fieldInputClass(fieldErrors.summary)} h-20 resize-none`}
              value={summary}
              onChange={(event) => setSummary(event.target.value)}
            />
          </FormField>
          <FormField label="Link (optional)" htmlFor="event-url" error={fieldErrors.url} className="col-span-2">
            <input
              id="event-url"
              type="url"
              className={fieldInputClass(fieldErrors.url)}
              value={url}
              maxLength={2048}
              onChange={(event) => setUrl(event.target.value)}
            />
          </FormField>
        </div>

        {generalError && (
          <p role="alert" className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            {generalError}
          </p>
        )}
        {sentTitle && (
          <p role="status" className="mt-3 rounded-md border border-green-200 bg-green-50 px-3 py-2 text-[13px] text-green-800">
            Event sent: {sentTitle}
          </p>
        )}

        <div className="mt-3.5 flex justify-end gap-2">
          <Button onClick={clear}>Clear</Button>
          <Button type="submit" variant="primary" disabled={simulateEvent.isPending}>
            {simulateEvent.isPending ? 'Sending…' : 'Send event'}
          </Button>
        </div>
      </form>
    </Section>
  )
}
