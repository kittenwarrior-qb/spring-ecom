import apiClient from '@/lib/api-client'
import type { ApiResponse, PageResponse, UserRequest, UserResponse } from '@/types/api'

const USER_BASE_URL = '/v1/api/users'

export const userApi = {
  // Get all users with pagination
  getAll: async (params?: { page?: number; size?: number; sort?: string }): Promise<PageResponse<UserResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<UserResponse>>>(USER_BASE_URL, { params })
    return response.data.data
  },

  // Get user by ID
  getById: async (userId: string): Promise<UserResponse> => {
    const response = await apiClient.get<ApiResponse<UserResponse>>(`/v1/api/user/${userId}`)
    return response.data.data
  },

  // Create new user
  create: async (data: UserRequest): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(USER_BASE_URL, data)
  },

  // Update user
  update: async (id: number, data: Partial<UserRequest>): Promise<UserResponse> => {
    const response = await apiClient.put<ApiResponse<UserResponse>>(`${USER_BASE_URL}/${id}`, data)
    return response.data.data
  },

  // Delete user
  delete: async (id: number): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${USER_BASE_URL}/${id}`)
  },
}
