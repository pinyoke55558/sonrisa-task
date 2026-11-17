export type CategoryDto = {
  id: number
  code: string
  name: string
  description: string | null
  version: number
}

export type CategorySaveRequest = {
  code: string
  name: string
  description?: string | null
}

export type CategoryUpdateRequest = CategorySaveRequest & {
  version: number
}
