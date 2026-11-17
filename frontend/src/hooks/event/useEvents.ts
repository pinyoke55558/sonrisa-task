import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { listEvents } from '../../services/eventService'

export const EVENTS_QUERY_KEY = ['events'] as const

export function useEvents(page: number, size: number) {
  return useQuery({
    queryKey: [...EVENTS_QUERY_KEY, page, size],
    queryFn: ({ signal }) => listEvents(page, size, signal),
    placeholderData: keepPreviousData,
  })
}
