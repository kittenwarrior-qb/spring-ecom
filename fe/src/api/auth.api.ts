import apiClient from '@/lib/api-client'
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest } from '@/types/api'

const AUTH_BASE_URL = '/v1/api/auth'

export const authApi = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(`${AUTH_BASE_URL}/login`, data)
    return response.data.data
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(`${AUTH_BASE_URL}/register`, data)
    return response.data.data
  },

  refresh: async (): Promise<AuthResponse> => {
    const response = await apiClient.post<ApiResponse<AuthResponse>>(`${AUTH_BASE_URL}/refresh`)
    return response.data.data
  },

  logout: async (): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(`${AUTH_BASE_URL}/logout`)
  },
}
