import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'

export function SimulateEventPage() {
  return (
    <PageLayout
      title="Simulate event"
      description="Send a test event through the dummy API. Rules that match it notify their targets."
    >
      <Section title="New event">
        <EmptyState message="The event form will be added here." />
      </Section>
      <Section title="Recently sent">
        <EmptyState message="No events sent yet." />
      </Section>
    </PageLayout>
  )
}
