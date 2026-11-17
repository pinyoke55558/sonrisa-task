import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateRule } from '../../services/ruleService'
import type { RuleUpdateRequest } from '../../types/rule'
import { RULES_QUERY_KEY } from './useRules'

type UpdateVariables = {
  id: number
  request: RuleUpdateRequest
}

export function useUpdateRule() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, request }: UpdateVariables) => updateRule(id, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: RULES_QUERY_KEY }),
  })
}
