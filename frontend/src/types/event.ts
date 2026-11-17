import type { CategoryDto } from './category'

export type EventDto = {
  id: number
  category: CategoryDto
  title: string
  summary: string | null
  url: string | null
  occurredAt: string
  createdAt: string
}

export type PageDto<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}
