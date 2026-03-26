import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { roleApi } from '@/api/role.api'
import { toast } from 'sonner'
import type { CreateRoleRequest, AssignPermissionsRequest } from '@/types/api'

export const roleKeys = {
  allRoles: ['roles'] as const,
  allPermissions: ['permissions'] as const,
  userRoles: (userId: number) => ['userRoles', userId] as const,
}

export function useRoles() {
  return useQuery({
    queryKey: roleKeys.allRoles,
    queryFn: roleApi.getAllRoles,
  })
}

export function usePermissions() {
  return useQuery({
    queryKey: roleKeys.allPermissions,
    queryFn: roleApi.getAllPermissions,
  })
}

export function useUserRoles(userId: number | null) {
  return useQuery({
    queryKey: roleKeys.userRoles(userId!),
    queryFn: () => roleApi.getUserRoles(userId!),
    enabled: !!userId,
  })
}

export function useCreateRole() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateRoleRequest) => roleApi.createRole(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: roleKeys.allRoles })
      toast.success('Role created successfully')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create role')
    },
  })
}

export function useAssignPermissions() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ roleId, data }: { roleId: number; data: AssignPermissionsRequest }) =>
      roleApi.assignPermissions(roleId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: roleKeys.allRoles })
      toast.success('Permissions assigned successfully')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to assign permissions')
    },
  })
}

export function useCreatePermission() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: { name: string }) => roleApi.createPermission(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: roleKeys.allPermissions })
      toast.success('Permission created successfully')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Failed to create permission')
    },
  })
}

// User Role Management Hooks
export function useSetUserRoles() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, roleIds }: { userId: number; roleIds: number[] }) =>
      roleApi.setUserRoles(userId, roleIds),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: roleKeys.userRoles(userId) })
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('Đã cập nhật vai trò thành công')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể cập nhật vai trò')
    },
  })
}

export function useAddRoleToUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: number; roleId: number }) =>
      roleApi.addRoleToUser(userId, roleId),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: roleKeys.userRoles(userId) })
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('Đã thêm vai trò thành công')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể thêm vai trò')
    },
  })
}

export function useRemoveRoleFromUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ userId, roleId }: { userId: number; roleId: number }) =>
      roleApi.removeRoleFromUser(userId, roleId),
    onSuccess: (_, { userId }) => {
      queryClient.invalidateQueries({ queryKey: roleKeys.userRoles(userId) })
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('Đã xóa vai trò thành công')
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Không thể xóa vai trò')
    },
  })
}
