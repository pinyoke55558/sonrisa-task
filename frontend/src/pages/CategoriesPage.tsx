import { useState } from 'react'
import type { TableColumn } from 'react-data-table-component'
import { keycloak } from '../auth/keycloak'
import { CategoryForm } from '../components/category/CategoryForm'
import { PageLayout, Section } from '../components/layout/PageLayout'
import { Button } from '../components/ui/Button'
import Table from '../components/ui/Table'
import { useCategories } from '../hooks/category/useCategories'
import type { CategoryDto } from '../types/category'

type Selection = { mode: 'new' } | { mode: 'edit'; category: CategoryDto } | null

const columns: TableColumn<CategoryDto>[] = [
  { name: 'Code', selector: (category) => category.code, width: '180px' },
  { name: 'Name', selector: (category) => category.name },
  { name: 'Description', selector: (category) => category.description ?? '—', grow: 2 },
]

export function CategoriesPage() {
  const isAdmin = keycloak.hasRealmRole('ADMIN')
  const { data: categories = [], isPending, error } = useCategories()
  const [selection, setSelection] = useState<Selection>(null)

  const selectedId = selection?.mode === 'edit' ? selection.category.id : undefined

  return (
    <PageLayout
      title="Categories"
      description={isAdmin ? 'Event categories that rules can filter on' : 'Event categories that your rules can filter on'}
    >
      <Section
        title="Categories"
        actions={
          isAdmin && (
            <Button variant="primary" size="small" onClick={() => setSelection({ mode: 'new' })}>
              New category
            </Button>
          )
        }
      >
        {error ? (
          <p role="alert" className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            Categories could not be loaded: {error.message}
          </p>
        ) : (
          <Table<CategoryDto>
            columns={columns}
            data={categories}
            keyField="id"
            progressPending={isPending}
            onClick={isAdmin ? (category) => setSelection({ mode: 'edit', category }) : undefined}
            conditionalRowStyles={[{ when: (category) => category.id === selectedId, classNames: ['rdt_rowSelected'] }]}
          />
        )}
      </Section>

      {isAdmin && selection && (
        <CategoryForm
          key={selection.mode === 'edit' ? `${selection.category.id}-${selection.category.version}` : 'new'}
          category={selection.mode === 'edit' ? selection.category : undefined}
          onSaved={(category) => setSelection({ mode: 'edit', category })}
          onCancel={() => setSelection(null)}
        />
      )}
    </PageLayout>
  )
}
