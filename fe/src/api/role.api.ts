import adminApiClient from '@/lib/admin-api-client'
import type {
  ApiResponse,
  RoleResponse,
  PermissionResponse,
  CreateRoleRequest,
  AssignPermissionsRequest,
} from '@/types/api'

export const roleApi = {
  getAllRoles: async () => {
    const response = await adminApiClient.get<ApiResponse<RoleResponse[]>>('/api/admin/roles')
    return response.data.data
  },

  getAllPermissions: async () => {
    const response = await adminApiClient.get<ApiResponse<PermissionResponse[]>>('/api/admin/roles/permissions')
    return response.data.data
  },

  createRole: async (data: CreateRoleRequest) => {
    const response = await adminApiClient.post<ApiResponse<RoleResponse>>('/api/admin/roles', data)
    return response.data.data
  },

  assignPermissions: async (roleId: number, data: AssignPermissionsRequest) => {
    const response = await adminApiClient.put<ApiResponse<RoleResponse>>(`/api/admin/roles/${roleId}/permissions`, data)
    return response.data.data
  },

  createPermission: async (data: { name: string }) => {
    const response = await adminApiClient.post<ApiResponse<PermissionResponse>>('/api/admin/roles/permissions', data)
    return response.data.data
  },

  // User Role Management APIs
  getUserRoles: async (userId: number) => {
    const response = await adminApiClient.get<ApiResponse<RoleResponse[]>>(`/api/admin/users/${userId}/roles`)
    return response.data.data
  },

  addRoleToUser: async (userId: number, roleId: number) => {
    await adminApiClient.post<ApiResponse<void>>(`/api/admin/users/${userId}/roles/${roleId}`)
  },

  removeRoleFromUser: async (userId: number, roleId: number) => {
    await adminApiClient.delete<ApiResponse<void>>(`/api/admin/users/${userId}/roles/${roleId}`)
  },

  setUserRoles: async (userId: number, roleIds: number[]) => {
    await adminApiClient.put<ApiResponse<void>>(`/api/admin/users/${userId}/roles`, { roleIds })
  },
}
