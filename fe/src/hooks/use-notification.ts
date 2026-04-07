import { useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { notificationApiClient } from '@/lib/api-client'
import { useMqttStore } from '@/lib/mqtt-client'
import type { ApiResponse, PageResponse } from '@/types/api'

interface NotificationResponse {
  id: number
  userId: number
  type: string
  title: string
  message: string
  referenceId: number | null
  referenceType: string | null
  imageUrl: string | null
  actionUrl: string | null
  isRead: boolean
  createdAt: string
}

const NOTIFICATION_BASE_URL = '/api/notifications'

export const notificationKeys = {
  all: ['notifications'] as const,
  lists: () => [...notificationKeys.all, 'list'] as const,
  list: (params: { page?: number; size?: number }) =>
    [...notificationKeys.lists(), params] as const,
  unread: () => [...notificationKeys.all, 'unread'] as const,
  unreadCount: () => [...notificationKeys.all, 'unreadCount'] as const,
}

// API functions
export const notificationApi = {
  getAll: async (page = 0, size = 20): Promise<PageResponse<NotificationResponse>> => {
    const response = await notificationApiClient.get<ApiResponse<PageResponse<NotificationResponse>>>(
      NOTIFICATION_BASE_URL,
      { params: { page, size } }
    )
    return response.data.data
  },

  getUnread: async (): Promise<NotificationResponse[]> => {
    const response = await notificationApiClient.get<ApiResponse<NotificationResponse[]>>(
      `${NOTIFICATION_BASE_URL}/unread`
    )
    return response.data.data
  },

  getUnreadCount: async (): Promise<number> => {
    const response = await notificationApiClient.get<ApiResponse<number>>(
      `${NOTIFICATION_BASE_URL}/unread/count`
    )
    return response.data.data
  },

  markAsRead: async (ids: number[]): Promise<void> => {
    await notificationApiClient.put(`${NOTIFICATION_BASE_URL}/read`, ids)
  },

  markAllAsRead: async (): Promise<void> => {
    await notificationApiClient.put(`${NOTIFICATION_BASE_URL}/read/all`)
  },
}

// Hooks
export function useNotifications(page = 0, size = 20) {
  return useQuery({
    queryKey: notificationKeys.list({ page, size }),
    queryFn: () => notificationApi.getAll(page, size),
  })
}

export function useUnreadNotifications() {
  return useQuery({
    queryKey: notificationKeys.unread(),
    queryFn: notificationApi.getUnread,
  })
}

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: notificationKeys.unreadCount(),
    queryFn: notificationApi.getUnreadCount,
    refetchInterval: 60000, // Refetch every minute
  })
}

export function useMarkNotificationAsRead() {
  const queryClient = useQueryClient()
  const { markAsRead } = useMqttStore()

  return useMutation({
    mutationFn: (ids: number[]) => notificationApi.markAsRead(ids),
    onSuccess: (_, ids) => {
      // Update local store - convert id to eventId string for MQTT notifications
      ids.forEach((id) => markAsRead(id.toString()))
      // Invalidate queries
      queryClient.invalidateQueries({ queryKey: notificationKeys.all })
    },
  })
}

export function useMarkAllNotificationsAsRead() {
  const queryClient = useQueryClient()
  const { markAllAsRead } = useMqttStore()

  return useMutation({
    mutationFn: notificationApi.markAllAsRead,
    onSuccess: () => {
      // Update local store
      markAllAsRead()
      // Invalidate queries
      queryClient.invalidateQueries({ queryKey: notificationKeys.all })
    },
  })
}

// MQTT connection hook
export function useNotificationMqtt(userId: number | null, token: string | null) {
  const { connect, disconnect, isConnected } = useMqttStore()

  // Connect when user is logged in - use useEffect to avoid multiple connections
  useEffect(() => {
    if (userId && token && !isConnected) {
      connect(userId, token)
    }

    // Disconnect on unmount or logout
    return () => {
      if (isConnected) {
        disconnect()
      }
    }
  }, [userId, token, isConnected, connect, disconnect])

  return {
    isConnected,
    disconnect,
  }
}
