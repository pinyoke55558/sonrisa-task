import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateCategory } from '../../services/categoryService'
import type { CategoryUpdateRequest } from '../../types/category'
import { CATEGORIES_QUERY_KEY } from './useCategories'

type UpdateVariables = {
  id: number
  request: CategoryUpdateRequest
}

export function useUpdateCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, request }: UpdateVariables) => updateCategory(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CATEGORIES_QUERY_KEY }),
  })
}
