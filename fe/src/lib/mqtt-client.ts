import mqtt from 'mqtt'
import type { MqttClient } from 'mqtt'
import { create } from 'zustand'

interface NotificationMessage {
  eventId: string
  eventType: string
  timestamp: string
  source: string
  notificationId: number | null
  userId: number | null
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

interface MqttState {
  client: MqttClient | null
  isConnected: boolean
  notifications: NotificationMessage[]
  unreadCount: number
  connect: (userId: number, token: string) => void
  disconnect: () => void
  addNotification: (notification: NotificationMessage) => void
  markAsRead: (eventId: string) => void
  markAllAsRead: () => void
}

const MQTT_WS_URL = import.meta.env.VITE_MQTT_WS_URL || 'ws://localhost:8083/mqtt'

export const useMqttStore = create<MqttState>((set, get) => ({
  client: null,
  isConnected: false,
  notifications: [],
  unreadCount: 0,

  connect: (userId: number, token: string) => {
    const { client: existingClient } = get()
    
    // Disconnect existing client if any
    if (existingClient) {
      existingClient.end()
    }

    const clientId = `user-${userId}-${Date.now()}`
    
    const newClient = mqtt.connect(MQTT_WS_URL, {
      clientId,
      username: `user-${userId}`,
      password: token,
      clean: true,
      reconnectPeriod: 5000,
      connectTimeout: 30000,
    })

    newClient.on('connect', () => {
      set({ isConnected: true })
      
      // Subscribe to user's notification topics
      newClient.subscribe(`notifications/${userId}/#`, (err) => {
        if (err) {
          // eslint-disable-next-line no-console
          console.error('[MQTT] Subscribe error:', err)
        }
      })
      
      // Subscribe to broadcast topics (for all users)
      newClient.subscribe('notifications/broadcast/#', (err) => {
        if (err) {
          // eslint-disable-next-line no-console
          console.error('[MQTT] Broadcast subscribe error:', err)
        }
      })
    })

    newClient.on('message', (_topic, payload) => {
      try {
        const notification: NotificationMessage = JSON.parse(payload.toString())
        
        set((state) => ({
          notifications: [notification, ...state.notifications].slice(0, 50),
          unreadCount: state.unreadCount + 1,
        }))
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error('[MQTT] Parse error:', error)
      }
    })

    newClient.on('error', (error) => {
      // eslint-disable-next-line no-console
      console.error('[MQTT] Error:', error)
    })

    newClient.on('close', () => {
      set({ isConnected: false })
    })

    newClient.on('reconnect', () => {
      // Silent reconnect
    })

    set({ client: newClient })
  },

  disconnect: () => {
    const { client } = get()
    if (client) {
      client.end()
      set({ client: null, isConnected: false })
    }
  },

  addNotification: (notification) => {
    set((state) => ({
      notifications: [notification, ...state.notifications].slice(0, 50),
      unreadCount: state.unreadCount + 1,
    }))
  },

  markAsRead: (eventId: string) => {
    set((state) => {
      const notifications = state.notifications.map((n) =>
        n.eventId === eventId ? { ...n, isRead: true } : n
      )
      const unreadCount = notifications.filter((n) => !n.isRead).length
      return { notifications, unreadCount }
    })
  },

  markAllAsRead: () => {
    set((state) => ({
      notifications: state.notifications.map((n) => ({ ...n, isRead: true })),
      unreadCount: 0,
    }))
  },
}))
