import { useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { notificationApiClient } from '@/lib/api-client'
import { useMqttStore } from '@/lib/mqtt-client'
import type { ApiResponse, PageResponse, NotificationResponse } from '@/types/api'

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
  const { markAllAsRead: markMqttAsRead } = useMqttStore()

  return useMutation({
    mutationFn: notificationApi.markAllAsRead,
    onMutate: async () => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: notificationKeys.all })

      // Snapshot previous value
      const previousNotifications = queryClient.getQueryData<PageResponse<NotificationResponse>>(
        notificationKeys.list({ page: 0, size: 20 })
      )

      // Optimistically update cache
      if (previousNotifications) {
        queryClient.setQueryData<PageResponse<NotificationResponse>>(
          notificationKeys.list({ page: 0, size: 20 }),
          {
            ...previousNotifications,
            content: previousNotifications.content.map((n) => ({ ...n, isRead: true })),
          }
        )
      }

      // Update MQTT store immediately
      markMqttAsRead()

      return { previousNotifications }
    },
    onError: (_err, _variables, context) => {
      // Rollback on error
      if (context?.previousNotifications) {
        queryClient.setQueryData(
          notificationKeys.list({ page: 0, size: 20 }),
          context.previousNotifications
        )
      }
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.all })
    },
  })
}

export function useNotificationMqtt(userId: number | null, token: string | null) {
  const { connect, disconnect, isConnected } = useMqttStore()

  // Connect when user is logged in - use useEffect to avoid multiple connections
  useEffect(() => {
    if (userId && token) {
      // Always try to connect if we have credentials
      // The connect function handles reconnection logic internally
      // eslint-disable-next-line no-console
      console.log('[MQTT Hook] Calling connect with userId:', userId)
      connect(userId, token)
    } else {
      // eslint-disable-next-line no-console
      console.log('[MQTT Hook] Missing userId or token, skipping connection')
    }

    // Only disconnect on unmount (cleanup function)
    return () => {
      disconnect()
    }
  }, [userId, token, connect, disconnect])

  // Debug log after state changes - use separate useEffect
  useEffect(() => {
    // eslint-disable-next-line no-console
    console.log('[MQTT Hook] userId:', userId, 'token:', token ? 'present' : 'null', 'isConnected:', isConnected)
  }, [userId, token, isConnected])

  return {
    isConnected,
    disconnect,
  }
}
