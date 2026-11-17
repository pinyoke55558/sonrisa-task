import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'

export function EventsPage() {
  return (
    <PageLayout title="Events" description="Events received from the event source, newest first">
      <Section title="Events">
        <EmptyState message="No events yet." />
      </Section>
    </PageLayout>
  )
}
