import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'

export function AllRulesPage() {
  return (
    <PageLayout title="All rules" description="Every user's rules, read only">
      <Section title="Rules">
        <EmptyState message="No rules yet." />
      </Section>
    </PageLayout>
  )
}
