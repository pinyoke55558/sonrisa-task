import type { RuleDto, RuleSaveRequest, RuleUpdateRequest } from '../types/rule'
import { axiosClient } from '../axios/axiosClient'

const BASE_URL = '/rules'

export async function listRules(signal?: AbortSignal): Promise<RuleDto[]> {
  const { data } = await axiosClient.get<RuleDto[]>(BASE_URL, { signal })
  return data
}

export async function listAllRules(signal?: AbortSignal): Promise<RuleDto[]> {
  const { data } = await axiosClient.get<RuleDto[]>(`${BASE_URL}/all`, { signal })
  return data
}

export async function loadRule(id: number, signal?: AbortSignal): Promise<RuleDto> {
  const { data } = await axiosClient.get<RuleDto>(`${BASE_URL}/${id}`, { signal })
  return data
}

export async function saveRule(request: RuleSaveRequest, signal?: AbortSignal): Promise<RuleDto> {
  const { data } = await axiosClient.post<RuleDto>(BASE_URL, request, { signal })
  return data
}

export async function updateRule(id: number, request: RuleUpdateRequest, signal?: AbortSignal): Promise<RuleDto> {
  const { data } = await axiosClient.put<RuleDto>(`${BASE_URL}/${id}`, request, { signal })
  return data
}

export async function deleteRule(id: number, signal?: AbortSignal): Promise<void> {
  await axiosClient.delete(`${BASE_URL}/${id}`, { signal })
}
