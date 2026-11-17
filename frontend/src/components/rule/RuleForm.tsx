import { type FormEvent, useState } from 'react'
import { ApiError } from '../../axios/axiosClient'
import { useCategories } from '../../hooks/category/useCategories'
import { useDeleteRule } from '../../hooks/rule/useDeleteRule'
import { useSaveRule } from '../../hooks/rule/useSaveRule'
import { useUpdateRule } from '../../hooks/rule/useUpdateRule'
import type { ChannelType, RuleDto, TargetRequest } from '../../types/rule'
import { fieldInputClass } from '../../utils/formStyles'
import { Section } from '../layout/PageLayout'
import { Button } from '../ui/Button'
import { FormField } from '../ui/FormField'

type RuleFormProps = {
  rule?: RuleDto
  onSaved: (rule: RuleDto) => void
  onDeleted: () => void
  onCancel: () => void
}

type TargetRow = TargetRequest & { key: number }

const DESTINATION_PLACEHOLDER: Record<ChannelType, string> = {
  EMAIL: 'anna.smith@example.com',
  SLACK: 'https://hooks.slack.com/services/…',
}

let nextTargetKey = 0

function toTargetRow(target: TargetRequest): TargetRow {
  nextTargetKey += 1
  return { key: nextTargetKey, channel: target.channel, destination: target.destination }
}

function parseKeywords(value: string) {
  return [...new Set(value.split(',').map((keyword) => keyword.trim()).filter(Boolean))]
}

export function RuleForm({ rule, onSaved, onDeleted, onCancel }: RuleFormProps) {
  const [name, setName] = useState(rule?.name ?? '')
  const [enabled, setEnabled] = useState(rule?.enabled ?? true)
  const [keywords, setKeywords] = useState(rule?.keywords.join(', ') ?? '')
  const [categoryIds, setCategoryIds] = useState<number[]>(rule?.categories.map((category) => category.id) ?? [])
  const [targets, setTargets] = useState<TargetRow[]>(
    (rule?.targets ?? [{ channel: 'EMAIL' as ChannelType, destination: '' }]).map(toTargetRow),
  )

  const { data: categories = [] } = useCategories()
  const saveRule = useSaveRule()
  const updateRule = useUpdateRule()
  const deleteRule = useDeleteRule()
  const mutation = rule ? updateRule : saveRule

  const failed = [mutation.error, deleteRule.error].find((error): error is ApiError => error instanceof ApiError)
  const fieldErrors = failed?.fieldErrors ?? {}
  const generalError = failed && Object.keys(fieldErrors).length === 0 ? failed.message : undefined
  const keywordsError = fieldErrors.keywords ?? Object.entries(fieldErrors).find(([field]) => field.startsWith('keywords['))?.[1]

  function updateTarget(key: number, change: Partial<TargetRequest>) {
    setTargets((rows) => rows.map((row) => (row.key === key ? { ...row, ...change } : row)))
  }

  function toggleCategory(id: number) {
    setCategoryIds((ids) => (ids.includes(id) ? ids.filter((categoryId) => categoryId !== id) : [...ids, id]))
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const request = {
      name: name.trim(),
      enabled,
      keywords: parseKeywords(keywords),
      categoryIds,
      targets: targets.map(({ channel, destination }) => ({ channel, destination: destination.trim() })),
    }
    const options = { onSuccess: onSaved }

    if (rule) {
      updateRule.mutate({ id: rule.id, request: { ...request, version: rule.version } }, options)
    } else {
      saveRule.mutate(request, options)
    }
  }

  function handleDelete() {
    if (rule && window.confirm(`Delete the rule "${rule.name}"?`)) {
      deleteRule.mutate(rule.id, { onSuccess: onDeleted })
    }
  }

  return (
    <Section title={rule ? 'Edit rule' : 'New rule'}>
      <form onSubmit={handleSubmit} noValidate>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Name" htmlFor="rule-name" error={fieldErrors.name}>
            <input
              id="rule-name"
              className={fieldInputClass(fieldErrors.name)}
              value={name}
              maxLength={100}
              onChange={(event) => setName(event.target.value)}
            />
          </FormField>

          <div className="flex items-end pb-2">
            <label className="flex cursor-pointer items-center gap-2 text-[13px]">
              <input type="checkbox" checked={enabled} onChange={(event) => setEnabled(event.target.checked)} />
              Enabled
            </label>
          </div>

          <FormField
            label="Keywords (comma-separated, at least one)"
            htmlFor="rule-keywords"
            error={keywordsError}
            className="col-span-2"
          >
            <input
              id="rule-keywords"
              className={fieldInputClass(keywordsError)}
              value={keywords}
              placeholder="earthquake, tsunami"
              onChange={(event) => setKeywords(event.target.value)}
            />
          </FormField>

          <fieldset className="col-span-2">
            <legend className="mb-1 text-xs text-muted">Categories (none selected = any category)</legend>
            {categories.length === 0 ? (
              <p className="text-[13px] text-muted">No categories yet.</p>
            ) : (
              <div className="flex flex-wrap gap-x-4 gap-y-1.5">
                {categories.map((category) => (
                  <label key={category.id} className="flex cursor-pointer items-center gap-1.5 text-[13px]">
                    <input
                      type="checkbox"
                      checked={categoryIds.includes(category.id)}
                      onChange={() => toggleCategory(category.id)}
                    />
                    {category.name}
                  </label>
                ))}
              </div>
            )}
            {fieldErrors.categoryIds && <p className="mt-1 text-xs text-red-600">{fieldErrors.categoryIds}</p>}
          </fieldset>

          <fieldset className="col-span-2">
            <legend className="mb-1 text-xs text-muted">Notification targets (at least one)</legend>
            <div className="flex flex-col gap-1.5 rounded-md border border-line p-2">
              {targets.map((target, index) => {
                const destinationError = fieldErrors[`targets[${index}].destination`]
                return (
                  <div key={target.key}>
                    <div className="grid grid-cols-[110px_1fr_auto] items-center gap-2">
                      <select
                        aria-label={`Target ${index + 1} channel`}
                        className={fieldInputClass()}
                        value={target.channel}
                        onChange={(event) => updateTarget(target.key, { channel: event.target.value as ChannelType })}
                      >
                        <option value="EMAIL">Email</option>
                        <option value="SLACK">Slack</option>
                      </select>
                      <input
                        aria-label={`Target ${index + 1} destination`}
                        className={fieldInputClass(destinationError)}
                        value={target.destination}
                        maxLength={2048}
                        placeholder={DESTINATION_PLACEHOLDER[target.channel]}
                        onChange={(event) => updateTarget(target.key, { destination: event.target.value })}
                      />
                      <Button
                        size="small"
                        disabled={targets.length === 1}
                        onClick={() => setTargets((rows) => rows.filter((row) => row.key !== target.key))}
                      >
                        Remove
                      </Button>
                    </div>
                    {destinationError && <p className="mt-1 text-xs text-red-600">{destinationError}</p>}
                  </div>
                )
              })}
              <div>
                <Button
                  size="small"
                  onClick={() => setTargets((rows) => [...rows, toTargetRow({ channel: 'EMAIL', destination: '' })])}
                >
                  Add target
                </Button>
              </div>
            </div>
            {fieldErrors.targets && <p className="mt-1 text-xs text-red-600">{fieldErrors.targets}</p>}
          </fieldset>
        </div>

        {generalError && (
          <p role="alert" className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            {generalError}
          </p>
        )}

        <div className="mt-3.5 flex justify-end gap-2">
          {rule && (
            <Button onClick={handleDelete} disabled={deleteRule.isPending}>
              {deleteRule.isPending ? 'Deleting…' : 'Delete'}
            </Button>
          )}
          <Button onClick={onCancel}>Cancel</Button>
          <Button type="submit" variant="primary" disabled={mutation.isPending}>
            {mutation.isPending ? 'Saving…' : rule ? 'Save rule' : 'Create rule'}
          </Button>
        </div>
      </form>
    </Section>
  )
}
