import { useState } from 'react'
import type { TableColumn } from 'react-data-table-component'
import { PageLayout, Section } from '../components/layout/PageLayout'
import { RuleForm } from '../components/rule/RuleForm'
import { Button } from '../components/ui/Button'
import Table from '../components/ui/Table'
import { useRules } from '../hooks/rule/useRules'
import type { RuleDto } from '../types/rule'

type Selection = { mode: 'new' } | { mode: 'edit'; rule: RuleDto } | null

const CHANNEL_LABEL = { EMAIL: 'Email', SLACK: 'Slack' } as const

function StatusPill({ enabled }: { enabled: boolean }) {
  return enabled ? (
    <span className="rounded-md bg-green-50 px-2 py-0.5 text-[11px] text-green-800">Enabled</span>
  ) : (
    <span className="rounded-md bg-hover px-2 py-0.5 text-[11px] text-muted">Disabled</span>
  )
}

const columns: TableColumn<RuleDto>[] = [
  { name: 'Name', selector: (rule) => rule.name },
  { name: 'Keywords', selector: (rule) => rule.keywords.join(', '), grow: 2 },
  {
    name: 'Categories',
    selector: (rule) => (rule.categories.length ? rule.categories.map((category) => category.name).join(', ') : 'Any'),
  },
  {
    name: 'Targets',
    selector: (rule) => [...new Set(rule.targets.map((target) => CHANNEL_LABEL[target.channel]))].join(', '),
  },
  { name: 'Status', cell: (rule) => <StatusPill enabled={rule.enabled} />, width: '110px' },
]

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
            columns={columns}
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
