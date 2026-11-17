import { useMutation, useQueryClient } from '@tanstack/react-query'
import { saveCategory } from '../../services/categoryService'
import type { CategorySaveRequest } from '../../types/category'
import { CATEGORIES_QUERY_KEY } from './useCategories'

export function useSaveCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: CategorySaveRequest) => saveCategory(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CATEGORIES_QUERY_KEY }),
  })
}
