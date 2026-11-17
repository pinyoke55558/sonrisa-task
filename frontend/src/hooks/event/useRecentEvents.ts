import { useQuery } from '@tanstack/react-query'
import { listRecentEvents } from '../../services/eventService'
import { EVENTS_QUERY_KEY } from './useEvents'

export function useRecentEvents(limit: number) {
  return useQuery({
    queryKey: [...EVENTS_QUERY_KEY, 'recent', limit],
    queryFn: ({ signal }) => listRecentEvents(limit, signal),
  })
}
