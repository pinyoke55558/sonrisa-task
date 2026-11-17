import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'
import { Button } from '../components/ui/Button'

export function RulesPage() {
  return (
    <PageLayout title="My rules" description="Get notified when an event contains your keywords">
      <Section
        title="Rules"
        actions={
          <Button variant="primary" size="small">New rule</Button>
        }
      >
        <EmptyState message="You have no rules yet." />
      </Section>
    </PageLayout>
  )
}
