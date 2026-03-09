import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { userApi } from '@/api/user.api'
import type { PageRequest, UserRequest, UserResponse } from '@/types/api'

// Query keys
export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (params: PageRequest) => [...userKeys.lists(), params] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: string) => [...userKeys.details(), id] as const,
}

// Get all users with pagination
export function useUsers(params?: PageRequest) {
  return useQuery({
    queryKey: userKeys.list(params || {}),
    queryFn: () => userApi.getAll(params),
  })
}

// Get user by ID
export function useUser(userId: string) {
  return useQuery({
    queryKey: userKeys.detail(userId),
    queryFn: () => userApi.getById(userId),
    enabled: !!userId,
  })
}

// Create user
export function useCreateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: UserRequest) => userApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
    },
  })
}

// Update user
export function useUpdateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<UserRequest> }) =>
      userApi.update(id, data),
    onSuccess: (updatedUser: UserResponse) => {
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
      queryClient.setQueryData(userKeys.detail(updatedUser.id.toString()), updatedUser)
    },
  })
}

// Delete user
export function useDeleteUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => userApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.lists() })
    },
  })
}
