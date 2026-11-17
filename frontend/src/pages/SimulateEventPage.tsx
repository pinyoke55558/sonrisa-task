import type { TableColumn } from 'react-data-table-component'
import { EventSimulationForm } from '../components/event/EventSimulationForm'
import { PageLayout, Section } from '../components/layout/PageLayout'
import Table from '../components/ui/Table'
import { useRecentEvents } from '../hooks/event/useRecentEvents'
import type { EventDto } from '../types/event'
import { formatDateTime } from '../utils/formatDate'

const RECENT_COUNT = 5

const columns: TableColumn<EventDto>[] = [
  { name: 'Title', selector: (event) => event.title, grow: 3 },
  { name: 'Category', selector: (event) => event.category.name, width: '160px' },
  { name: 'Occurred at', selector: (event) => formatDateTime(event.occurredAt), width: '170px' },
]

export function SimulateEventPage() {
  const { data = [], isPending, error } = useRecentEvents(RECENT_COUNT)

  return (
    <PageLayout
      title="Simulate event"
      description="Send a test event through the dummy API. Rules that match it notify their targets."
    >
      <EventSimulationForm />
      <Section title="Recently sent">
        {error ? (
          <p role="alert" className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            Events could not be loaded: {error.message}
          </p>
        ) : (
          <Table<EventDto> columns={columns} data={data} keyField="id" progressPending={isPending} />
        )}
      </Section>
    </PageLayout>
  )
}
