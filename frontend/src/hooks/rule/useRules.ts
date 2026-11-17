import { useQuery } from '@tanstack/react-query'
import { listRules } from '../../services/ruleService'

export const RULES_QUERY_KEY = ['rules'] as const

export function useRules() {
  return useQuery({
    queryKey: RULES_QUERY_KEY,
    queryFn: ({ signal }) => listRules(signal),
  })
}
