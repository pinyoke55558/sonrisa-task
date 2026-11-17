import axios, { AxiosError } from 'axios'
import { keycloak } from '../auth/keycloak'

type ErrorBody = {
  detail?: string
  title?: string
  message?: string
  error?: string
  errors?: Record<string, string>
}

export class ApiError extends Error {
  readonly status: number
  readonly fieldErrors: Record<string, string>

  constructor(status: number, message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

export const axiosClient = axios.create({
  baseURL: '/api',
})

axiosClient.interceptors.request.use(async (config) => {
  try {
    await keycloak.updateToken(30)
  } catch {
    await keycloak.login()
  }
  config.headers.Authorization = `Bearer ${keycloak.token}`
  return config
})

axiosClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isCancel(error) || !(error instanceof AxiosError)) return Promise.reject(error)
    const data = error.response?.data as ErrorBody | undefined
    const message = data?.detail || data?.title || data?.message || data?.error || error.message
    return Promise.reject(new ApiError(error.response?.status ?? 0, message, data?.errors))
  },
)
