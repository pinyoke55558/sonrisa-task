import { EmptyState, PageLayout, Section } from '../components/layout/PageLayout'

export function CategoriesPage() {
  return (
    <PageLayout title="Categories" description="Event categories that rules can filter on">
      <Section
        title="Categories"
        actions={
          <button type="button" className="button primary small">
            New category
          </button>
        }
      >
        <EmptyState message="No categories yet." />
      </Section>
    </PageLayout>
  )
}
