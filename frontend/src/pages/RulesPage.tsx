import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'

export function RulesPage() {
  return (
    <PageLayout title="My rules" description="Get notified when an event contains your keywords">
      <Section
        title="Rules"
        actions={
          <button type="button" className="button primary small">
            New rule
          </button>
        }
      >
        <EmptyState message="You have no rules yet." />
      </Section>
    </PageLayout>
  )
}
