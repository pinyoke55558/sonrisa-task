import { type FormEvent, useState } from 'react'
import { ApiError } from '../../axios/axiosClient'
import { useSaveCategory } from '../../hooks/category/useSaveCategory'
import { useUpdateCategory } from '../../hooks/category/useUpdateCategory'
import type { CategoryDto } from '../../types/category'
import { Section } from '../layout/PageLayout'
import { Button } from '../ui/Button'
import { FormField } from '../ui/FormField'
import { fieldInputClass } from '../../utils/formStyles'

type CategoryFormProps = {
  category?: CategoryDto
  onSaved: (category: CategoryDto) => void
  onCancel: () => void
}

export function CategoryForm({ category, onSaved, onCancel }: CategoryFormProps) {
  const [code, setCode] = useState(category?.code ?? '')
  const [name, setName] = useState(category?.name ?? '')
  const [description, setDescription] = useState(category?.description ?? '')

  const saveCategory = useSaveCategory()
  const updateCategory = useUpdateCategory()
  const mutation = category ? updateCategory : saveCategory

  const error = mutation.error instanceof ApiError ? mutation.error : undefined
  const fieldErrors = error?.fieldErrors ?? {}
  const generalError = error && Object.keys(fieldErrors).length === 0 ? error.message : undefined

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const request = { code: code.trim(), name: name.trim(), description: description.trim() || null }
    const options = { onSuccess: onSaved }

    if (category) {
      updateCategory.mutate({ id: category.id, request: { ...request, version: category.version } }, options)
    } else {
      saveCategory.mutate(request, options)
    }
  }

  return (
    <Section title={category ? 'Edit category' : 'New category'}>
      <form onSubmit={handleSubmit} noValidate>
        <div className="grid grid-cols-2 gap-3">
          <FormField label="Code" htmlFor="category-code" error={fieldErrors.code}>
            <input
              id="category-code"
              className={fieldInputClass(fieldErrors.code)}
              value={code}
              maxLength={50}
              onChange={(event) => setCode(event.target.value)}
            />
          </FormField>
          <FormField label="Name" htmlFor="category-name" error={fieldErrors.name}>
            <input
              id="category-name"
              className={fieldInputClass(fieldErrors.name)}
              value={name}
              maxLength={100}
              onChange={(event) => setName(event.target.value)}
            />
          </FormField>
          <FormField
            label="Description (optional)"
            htmlFor="category-description"
            error={fieldErrors.description}
            className="col-span-2"
          >
            <textarea
              id="category-description"
              className={`${fieldInputClass(fieldErrors.description)} h-16 resize-none`}
              value={description}
              maxLength={500}
              onChange={(event) => setDescription(event.target.value)}
            />
          </FormField>
        </div>

        {generalError && (
          <p role="alert" className="mt-3 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-[13px] text-red-700">
            {generalError}
          </p>
        )}

        <div className="mt-3.5 flex justify-end gap-2">
          <Button onClick={onCancel}>Cancel</Button>
          <Button type="submit" variant="primary" disabled={mutation.isPending}>
            {mutation.isPending ? 'Saving…' : category ? 'Save category' : 'Create category'}
          </Button>
        </div>
      </form>
    </Section>
  )
}
