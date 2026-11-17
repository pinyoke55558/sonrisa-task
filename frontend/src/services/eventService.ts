import type { EventDto, PageDto } from '../types/event'
import { axiosClient } from '../axios/axiosClient'

const BASE_URL = '/events'

export async function listEvents(page: number, size: number, signal?: AbortSignal): Promise<PageDto<EventDto>> {
  const { data } = await axiosClient.get<PageDto<EventDto>>(BASE_URL, { params: { page, size }, signal })
  return data
}

export async function loadEvent(id: number, signal?: AbortSignal): Promise<EventDto> {
  const { data } = await axiosClient.get<EventDto>(`${BASE_URL}/${id}`, { signal })
  return data
}
