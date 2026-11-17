import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'
import { Button } from '../components/ui/Button'

export function CategoriesPage() {
  return (
    <PageLayout title="Categories" description="Event categories that rules can filter on">
      <Section
        title="Categories"
        actions={
          <Button variant="primary" size="small">New category</Button>
        }
      >
        <EmptyState message="No categories yet." />
      </Section>
    </PageLayout>
  )
}
