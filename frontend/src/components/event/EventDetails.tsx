import type { EventDto } from '../../types/event'
import { formatDateTime } from '../../utils/formatDate'
import { Section } from '../layout/PageLayout'
import { Button } from '../ui/Button'
import { Detail } from '../ui/Detail'

type EventDetailsProps = {
  event: EventDto
  onClose: () => void
}

export function EventDetails({ event, onClose }: EventDetailsProps) {
  return (
    <Section title="Event details" actions={<Button size="small" onClick={onClose}>Close</Button>}>
      <dl className="m-0 grid grid-cols-2 gap-3">
        <div className="col-span-2">
          <Detail label="Title">{event.title}</Detail>
        </div>
        <Detail label="Category">{event.category.name}</Detail>
        <Detail label="Occurred at">{formatDateTime(event.occurredAt)}</Detail>
        <Detail label="Received at">{formatDateTime(event.createdAt)}</Detail>
        <Detail label="Link">
          {event.url ? (
            <a href={event.url} target="_blank" rel="noreferrer" className="break-all text-accent hover:underline">
              {event.url}
            </a>
          ) : (
            '—'
          )}
        </Detail>
        <div className="col-span-2">
          <Detail label="Summary">
            <span className="whitespace-pre-wrap">{event.summary || '—'}</span>
          </Detail>
        </div>
      </dl>
    </Section>
  )
}
