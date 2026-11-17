import type { ReactNode } from 'react'
import type { RuleDto } from '../../types/rule'
import { CHANNEL_LABEL, formatCategories } from '../../utils/ruleColumns'
import { Section } from '../layout/PageLayout'
import { Button } from '../ui/Button'
import { StatusPill } from './StatusPill'

function Detail({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <dt className="mb-0.5 text-xs text-muted">{label}</dt>
      <dd className="m-0 text-[13px]">{children}</dd>
    </div>
  )
}

type RuleDetailsProps = {
  rule: RuleDto
  onClose: () => void
}

export function RuleDetails({ rule, onClose }: RuleDetailsProps) {
  return (
    <Section title="Rule details" actions={<Button size="small" onClick={onClose}>Close</Button>}>
      <dl className="m-0 grid grid-cols-2 gap-3">
        <Detail label="Name">{rule.name}</Detail>
        <Detail label="Owner">{rule.owner ?? '—'}</Detail>
        <Detail label="Keywords">{rule.keywords.join(', ')}</Detail>
        <Detail label="Categories">{formatCategories(rule)}</Detail>
        <Detail label="Status">
          <StatusPill enabled={rule.enabled} />
        </Detail>
        <div className="col-span-2">
          <Detail label="Notification targets">
            <ul className="m-0 flex list-none flex-col gap-1 p-0">
              {rule.targets.map((target) => (
                <li key={target.id} className="flex gap-2">
                  <span className="w-12 text-muted">{CHANNEL_LABEL[target.channel]}</span>
                  <span className="break-all">{target.destination}</span>
                </li>
              ))}
            </ul>
          </Detail>
        </div>
      </dl>
    </Section>
  )
}
