import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { userApi } from '@/api/user.api'
import { getCookie } from '@/lib/cookies'
import type { PageRequest, UserRequest, UserResponse, UpdateProfileRequest, UpdateAvatarRequest, ChangePasswordRequest } from '@/types/api'

// Query keys
export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (params: PageRequest) => [...userKeys.lists(), params] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: string) => [...userKeys.details(), id] as const,
  profile: () => [...userKeys.all, 'profile'] as const,
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

// Profile hooks
// Get current user profile
export function useUserProfile() {
  // Only fetch profile if user has access token
  const hasToken = !!getCookie('accessToken')
  
  return useQuery({
    queryKey: userKeys.profile(),
    queryFn: userApi.getProfile,
    enabled: hasToken, // Only run query if user is authenticated
    retry: false, // Don't retry on failure to avoid infinite loop
    staleTime: 5 * 60 * 1000, // Cache for 5 minutes
  })
}

// Update profile
export function useUpdateProfile() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => userApi.updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.profile() })
    },
  })
}

// Update avatar
export function useUpdateAvatar() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: UpdateAvatarRequest) => userApi.updateAvatar(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userKeys.profile() })
    },
  })
}

// Change password
export function useChangePassword() {
  return useMutation({
    mutationFn: (data: ChangePasswordRequest) => userApi.changePassword(data),
  })
}
