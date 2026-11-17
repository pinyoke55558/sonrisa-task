import { useMutation, useQueryClient } from '@tanstack/react-query'
import { simulateEvent } from '../../services/eventService'
import type { EventSimulationRequest } from '../../types/event'
import { EVENTS_QUERY_KEY } from './useEvents'

export function useSimulateEvent() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (request: EventSimulationRequest) => simulateEvent(request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: EVENTS_QUERY_KEY }),
  })
}
