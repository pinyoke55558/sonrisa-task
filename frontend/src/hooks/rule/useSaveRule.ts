import { useMutation, useQueryClient } from '@tanstack/react-query'
import { saveRule } from '../../services/ruleService'
import type { RuleSaveRequest } from '../../types/rule'
import { RULES_QUERY_KEY } from './useRules'

export function useSaveRule() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: RuleSaveRequest) => saveRule(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: RULES_QUERY_KEY }),
  })
}
