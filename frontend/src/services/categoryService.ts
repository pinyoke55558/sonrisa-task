import type { CategoryDto, CategorySaveRequest, CategoryUpdateRequest } from '../types/category'
import { axiosClient } from '../axios/axiosClient'

const BASE_URL = '/categories'

export async function listCategories(signal?: AbortSignal): Promise<CategoryDto[]> {
  const { data } = await axiosClient.get<CategoryDto[]>(BASE_URL, { signal })
  return data
}

export async function loadCategory(id: number, signal?: AbortSignal): Promise<CategoryDto> {
  const { data } = await axiosClient.get<CategoryDto>(`${BASE_URL}/${id}`, { signal })
  return data
}

export async function saveCategory(request: CategorySaveRequest, signal?: AbortSignal): Promise<CategoryDto> {
  const { data } = await axiosClient.post<CategoryDto>(BASE_URL, request, { signal })
  return data
}

export async function updateCategory(
  id: number,
  request: CategoryUpdateRequest,
  signal?: AbortSignal,
): Promise<CategoryDto> {
  const { data } = await axiosClient.put<CategoryDto>(`${BASE_URL}/${id}`, request, { signal })
  return data
}

export async function deleteCategory(id: number, signal?: AbortSignal): Promise<void> {
  await axiosClient.delete(`${BASE_URL}/${id}`, { signal })
}
