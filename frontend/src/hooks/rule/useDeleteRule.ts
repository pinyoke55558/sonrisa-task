import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteRule } from '../../services/ruleService'
import { RULES_QUERY_KEY } from './useRules'

export function useDeleteRule() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteRule(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: RULES_QUERY_KEY }),
  })
}
