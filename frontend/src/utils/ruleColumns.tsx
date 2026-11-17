import type { TableColumn } from 'react-data-table-component'
import { StatusPill } from '../components/rule/StatusPill'
import type { RuleDto } from '../types/rule'

export const CHANNEL_LABEL = { EMAIL: 'Email', SLACK: 'Slack' } as const

export function formatCategories(rule: RuleDto) {
  return rule.categories.length ? rule.categories.map((category) => category.name).join(', ') : 'Any'
}

export const ruleColumns: TableColumn<RuleDto>[] = [
  { name: 'Name', selector: (rule) => rule.name },
  { name: 'Keywords', selector: (rule) => rule.keywords.join(', '), grow: 2 },
  { name: 'Categories', selector: formatCategories },
  {
    name: 'Targets',
    selector: (rule) => [...new Set(rule.targets.map((target) => CHANNEL_LABEL[target.channel]))].join(', '),
  },
  { name: 'Status', cell: (rule) => <StatusPill enabled={rule.enabled} />, width: '110px' },
]

export const ownerColumn: TableColumn<RuleDto> = { name: 'Owner', selector: (rule) => rule.owner ?? '—' }
