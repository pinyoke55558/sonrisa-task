import { useState } from 'react'
import type { TableColumn } from 'react-data-table-component'
import { EventDetails } from '../components/event/EventDetails'
import { PageLayout, Section } from '../components/layout/PageLayout'
import Table from '../components/ui/Table'
import { useEvents } from '../hooks/event/useEvents'
import type { EventDto } from '../types/event'
import { formatDateTime } from '../utils/formatDate'

const PAGE_SIZES = [10, 20, 50, 100]

const columns: TableColumn<EventDto>[] = [
  { name: 'Occurred at', selector: (event) => formatDateTime(event.occurredAt), width: '170px' },
  { name: 'Category', selector: (event) => event.category.name, width: '160px' },
  { name: 'Title', selector: (event) => event.title, grow: 3 },
]

export function EventsPage() {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const { data, isPending, isFetching, error } = useEvents(page, size)
  const [selected, setSelected] = useState<EventDto | null>(null)

  return (
    <PageLayout title="Events" description="Events received from the event source, newest first">
      <Section title="Events">
        {error ? (
          <p role="alert" className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            Events could not be loaded: {error.message}
          </p>
        ) : (
          <Table<EventDto>
            columns={columns}
            data={data?.content ?? []}
            keyField="id"
            progressPending={isPending || isFetching}
            pagination
            paginationServer
            paginationTotalRows={data?.totalElements ?? 0}
            paginationPerPage={size}
            paginationRowsPerPageOptions={PAGE_SIZES}
            onChangePage={(newPage) => setPage(newPage - 1)}
            onChangeRowsPerPage={(newSize) => {
              setSize(newSize)
              setPage(0)
            }}
            onClick={setSelected}
            conditionalRowStyles={[{ when: (event) => event.id === selected?.id, classNames: ['rdt_rowSelected'] }]}
          />
        )}
      </Section>

      {selected && <EventDetails event={selected} onClose={() => setSelected(null)} />}
    </PageLayout>
  )
}
