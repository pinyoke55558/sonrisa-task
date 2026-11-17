import { useState } from 'react'
import { PageLayout, Section } from '../components/layout/PageLayout'
import { RuleForm } from '../components/rule/RuleForm'
import { Button } from '../components/ui/Button'
import Table from '../components/ui/Table'
import { useRules } from '../hooks/rule/useRules'
import type { RuleDto } from '../types/rule'
import { ruleColumns } from '../utils/ruleColumns'

type Selection = { mode: 'new' } | { mode: 'edit'; rule: RuleDto } | null

export function RulesPage() {
  const { data: rules = [], isPending, error } = useRules()
  const [selection, setSelection] = useState<Selection>(null)

  const selectedId = selection?.mode === 'edit' ? selection.rule.id : undefined

  return (
    <PageLayout title="My rules" description="Get notified when an event contains your keywords">
      <Section
        title="Rules"
        actions={
          <Button variant="primary" size="small" onClick={() => setSelection({ mode: 'new' })}>
            New rule
          </Button>
        }
      >
        {error ? (
          <p role="alert" className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            Rules could not be loaded: {error.message}
          </p>
        ) : (
          <Table<RuleDto>
            columns={ruleColumns}
            data={rules}
            keyField="id"
            progressPending={isPending}
            onClick={(rule) => setSelection({ mode: 'edit', rule })}
            conditionalRowStyles={[{ when: (rule) => rule.id === selectedId, classNames: ['rdt_rowSelected'] }]}
          />
        )}
      </Section>

      {selection && (
        <RuleForm
          key={selection.mode === 'edit' ? `${selection.rule.id}-${selection.rule.version}` : 'new'}
          rule={selection.mode === 'edit' ? selection.rule : undefined}
          onSaved={(rule) => setSelection({ mode: 'edit', rule })}
          onDeleted={() => setSelection(null)}
          onCancel={() => setSelection(null)}
        />
      )}
    </PageLayout>
  )
}
