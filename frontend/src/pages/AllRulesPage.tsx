import { useState } from 'react'
import { PageLayout, Section } from '../components/layout/PageLayout'
import { RuleDetails } from '../components/rule/RuleDetails'
import Table from '../components/ui/Table'
import { useAllRules } from '../hooks/rule/useAllRules'
import type { RuleDto } from '../types/rule'
import { ownerColumn, ruleColumns } from '../utils/ruleColumns'

const columns = [ownerColumn, ...ruleColumns]

export function AllRulesPage() {
  const { data: rules = [], isPending, error } = useAllRules()
  const [selected, setSelected] = useState<RuleDto | null>(null)

  return (
    <PageLayout title="All rules" description="Every user's rules, read only">
      <Section title="Rules">
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
            onClick={setSelected}
            conditionalRowStyles={[{ when: (rule) => rule.id === selected?.id, classNames: ['rdt_rowSelected'] }]}
          />
        )}
      </Section>

      {selected && <RuleDetails rule={selected} onClose={() => setSelected(null)} />}
    </PageLayout>
  )
}
