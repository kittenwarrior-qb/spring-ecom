import apiClient from '@/lib/api-client'
import type { ApiResponse, PageResponse, UserRequest, UserResponse, UserProfileResponse, UpdateProfileRequest, UpdateAvatarRequest, ChangePasswordRequest, UserSessionResponse } from '@/types/api'

const USER_BASE_URL = '/v1/api/users'
const PROFILE_BASE_URL = '/v1/api/user'

export const userApi = {
  // Get all users with pagination
  getAll: async (params?: { page?: number; size?: number; sort?: string }): Promise<PageResponse<UserResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<UserResponse>>>(USER_BASE_URL, { params })
    return response.data.data
  },

  // Get user by ID
  getById: async (userId: string): Promise<UserResponse> => {
    const response = await apiClient.get<ApiResponse<UserResponse>>(`${PROFILE_BASE_URL}/${userId}`)
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

  // Profile APIs
  // Get current user profile
  getProfile: async (): Promise<UserProfileResponse> => {
    const response = await apiClient.get<ApiResponse<UserProfileResponse>>(`${PROFILE_BASE_URL}/me`)
    return response.data.data
  },

  // Update profile
  updateProfile: async (data: UpdateProfileRequest): Promise<UserProfileResponse> => {
    const response = await apiClient.put<ApiResponse<UserProfileResponse>>(`${PROFILE_BASE_URL}/me`, data)
    return response.data.data
  },

  // Update avatar
  updateAvatar: async (data: UpdateAvatarRequest): Promise<UserProfileResponse> => {
    const response = await apiClient.put<ApiResponse<UserProfileResponse>>(`${PROFILE_BASE_URL}/me/avatar`, data)
    return response.data.data
  },

  // Change password
  changePassword: async (data: ChangePasswordRequest): Promise<void> => {
    await apiClient.put<ApiResponse<void>>(`${PROFILE_BASE_URL}/me/password`, data)
  },

  // Get current user session info
  getSession: async (): Promise<UserSessionResponse> => {
    const response = await apiClient.get<ApiResponse<UserSessionResponse>>(`${PROFILE_BASE_URL}/me/session`)
    return response.data.data
  },
}
