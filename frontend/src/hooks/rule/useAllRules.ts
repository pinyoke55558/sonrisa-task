import { useQuery } from '@tanstack/react-query'
import { listAllRules } from '../../services/ruleService'
import { RULES_QUERY_KEY } from './useRules'

export const ALL_RULES_QUERY_KEY = [...RULES_QUERY_KEY, 'all'] as const

export function useAllRules() {
  return useQuery({
    queryKey: ALL_RULES_QUERY_KEY,
    queryFn: ({ signal }) => listAllRules(signal),
  })
}
