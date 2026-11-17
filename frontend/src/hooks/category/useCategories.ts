import { useQuery } from '@tanstack/react-query'
import { listCategories } from '../../services/categoryService'

export const CATEGORIES_QUERY_KEY = ['categories'] as const

export function useCategories() {
  return useQuery({
    queryKey: CATEGORIES_QUERY_KEY,
    queryFn: ({ signal }) => listCategories(signal),
  })
}
