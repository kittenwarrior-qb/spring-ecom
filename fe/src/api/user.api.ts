import apiClient from '@/lib/api-client'
import adminApiClient from '@/lib/admin-api-client'
import type { ApiResponse, PageResponse, UserRequest, UserResponse, UserProfileResponse, UpdateProfileRequest, UpdateAvatarRequest, ChangePasswordRequest, RoleResponse } from '@/types/api'

const USER_BASE_URL = '/api/users'
const PROFILE_BASE_URL = '/api/users'

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
    await adminApiClient.delete<ApiResponse<void>>(`/admin/users/${id}`)
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

  // Admin APIs
  assignRole: async (userId: number, roleId: number): Promise<void> => {
    await adminApiClient.post<ApiResponse<void>>(`/api/admin/users/${userId}/roles/${roleId}`)
  },

  // Admin: Update user status (activate/deactivate)
  updateUserStatus: async (userId: number, isActive: boolean, reason?: string): Promise<void> => {
    await adminApiClient.put<ApiResponse<void>>(`/api/admin/users/${userId}/status`, null, {
      params: { isActive, ...(reason && { reason }) }
    })
  },

  // Admin: Reset user password
  resetUserPassword: async (userId: number): Promise<void> => {
    await adminApiClient.post<ApiResponse<void>>(`/api/admin/users/${userId}/reset-password`)
  },

  // Admin: Get user's roles
  getUserRoles: async (userId: number): Promise<RoleResponse[]> => {
    const response = await adminApiClient.get<ApiResponse<RoleResponse[]>>(`/api/admin/users/${userId}/roles`)
    return response.data.data
  },

  // Admin: Set user roles (replace all)
  setUserRoles: async (userId: number, roleIds: number[]): Promise<void> => {
    await adminApiClient.put<ApiResponse<void>>(`/api/admin/users/${userId}/roles`, { roleIds })
  },

  // Admin: Search users by email
  searchUsersByEmail: async (email: string, page = 0, size = 10): Promise<PageResponse<UserResponse>> => {
    const response = await adminApiClient.get<ApiResponse<PageResponse<UserResponse>>>(
      '/api/admin/users/search',
      { params: { email, page, size } }
    )
    return response.data.data
  },
}
